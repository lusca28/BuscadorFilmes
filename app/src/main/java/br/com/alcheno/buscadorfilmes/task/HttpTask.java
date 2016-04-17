package br.com.alcheno.buscadorfilmes.task;

import android.os.AsyncTask;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.net.URLEncoder;

public class HttpTask extends AsyncTask<String, Void, Object> {

    private volatile boolean running = true;
    private IInterface service;
    private boolean problema;
    private String msgErro, link, parametro, tipoChamada;
    private boolean chamadaColchetes = false;
    private int page = 1;
    private int quantidade;

    public HttpTask(IInterface service, String link, String parametro, String tipoChamada) {
        this.service = service;
        this.link = link;
        this.parametro = parametro;
        this.tipoChamada = tipoChamada;
    }

    public HttpTask(IInterface service, String link, String parametro, String tipoChamada, boolean chamadaColchetes, int page) {
        this.service = service;
        this.link = link;
        this.parametro = parametro;
        this.tipoChamada = tipoChamada;
        this.chamadaColchetes = chamadaColchetes;
        this.page = page;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected Object doInBackground(String... args) {

        while (running) {
            Object sucesso = null;
            HttpClient httpClient = new DefaultHttpClient();

            try {
                String query = "";
                if (!chamadaColchetes) {
                    query = URLEncoder.encode(parametro, "utf-8");
                } else if(page > 1){
                    query = URLEncoder.encode("{" + parametro + "}", "utf-8") + "&page=" + page;
                } else {
                    query = URLEncoder.encode("{" + parametro + "}", "utf-8");
                }
                String url = link + "/?" + tipoChamada + "=" + query;
                HttpGet request = new HttpGet(url);
                HttpResponse response = httpClient.execute(request);

                HttpEntity entity = response.getEntity();
                if (entity != null) {
                    String retSrc = EntityUtils.toString(entity);

                    JsonParser parser = new JsonParser();
                    JsonObject result = parser.parse(retSrc).getAsJsonObject();

                    JsonElement objeto = result;

                    if (chamadaColchetes) {
                        objeto = result.get("Search");
                        JsonElement qnt = result.get("totalResults");
                        quantidade = qnt.getAsInt();
                    }

                    if (objeto.isJsonArray()) {
                        sucesso = objeto.getAsJsonArray();

                    } else {
                        sucesso = objeto.getAsJsonObject();
                    }
                }
            } catch (Exception ex) {
                problema = true;
                msgErro = "catch: " + ex.getMessage();
            } finally {
                httpClient.getConnectionManager().shutdown();
            }

            return sucesso;
        }

        return null;
    }

    protected void onPostExecute(Object result) {
        if (problema) {
            service.processFinishErro(msgErro);
        } else {
            if (chamadaColchetes) {
                service.processFinish(result, quantidade);
            } else {
                service.processFinish(result);
            }
        }
    }
}

