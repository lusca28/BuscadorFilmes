package br.com.alcheno.buscadorfilmes.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.SyncStateContract;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;

import java.lang.reflect.Type;
import java.util.List;

import br.com.alcheno.buscadorfilmes.activity.Detalhes;
import br.com.alcheno.buscadorfilmes.task.HttpTask;
import br.com.alcheno.buscadorfilmes.task.IInterface;
import br.com.alcheno.buscadorfilmes.R;
import br.com.alcheno.buscadorfilmes.to.TOFilme;
import br.com.alcheno.buscadorfilmes.to.TOFilmeCompleto;

/**
 * Created by lusca on 16/04/2016.
 */
public class CardAdapter extends RecyclerView.Adapter<CardHolder> {

    private List<TOFilme> filmeList;
    private Activity context;
    private ImageLoader imageLoader;

    public CardAdapter(List<TOFilme> filmeList, Activity context, ImageLoader imageLoader) {
        this.filmeList = filmeList;
        this.context = context;
        this.imageLoader = imageLoader;
    }

    @Override
    public int getItemCount() {
        return filmeList.size();
    }

    @Override
    public CardHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view, parent, false);

        return new CardHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final CardHolder holder, int position) {
        final TOFilme fl = filmeList.get(position);
        holder.titulo.setText(fl.getTitle());

        if (!fl.getPoster().equals("N/A")) {
            imageLoader.displayImage(fl.getPoster(), holder.poster);
        }

        IInterface service = new IInterface() {
            @Override
            public void processFinish(Object output) {
                JsonParser jsonParser = new JsonParser();
                JsonObject gsonObj = jsonParser.parse(output.toString()).getAsJsonObject();

                Type listOfTestObject = new TypeToken<TOFilmeCompleto>(){}.getType();
                Gson gson = new Gson();
                final TOFilmeCompleto filmeCompleto = gson.fromJson(gsonObj, listOfTestObject);

                holder.descricao.setText(filmeCompleto.getPlot());

                //share
                holder.btShare.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                        sharingIntent.putExtra(Intent.EXTRA_SUBJECT, filmeCompleto.getTitle());
                        sharingIntent.putExtra(Intent.EXTRA_TEXT, filmeCompleto.getPlot());
                        sharingIntent.setType("image/*");
                        sharingIntent.putExtra(Intent.EXTRA_STREAM, filmeCompleto.getPoster());
                        context.startActivity(Intent.createChooser(sharingIntent, "Share via"));
                    }
                });

                //detalhes
                holder.card_view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        context.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

                        Intent it = new Intent(context, Detalhes.class);
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("filme", filmeCompleto);
                        it.putExtras(bundle);
                        context.startActivity(it);
                        context.overridePendingTransition(R.anim.slide_bottom_top, R.anim.slide_nothing);
                    }
                });
            }

            @Override
            public void processFinish(Object output, int quantidade) {

            }

            @Override
            public void processFinishErro(String output) {
//                Toast.makeText(context, output, Toast.LENGTH_LONG).show();
            }
        };
        HttpTask task = new HttpTask(service, context.getString(R.string.link_filmes_disponiveis), fl.getImdbID(), "i");
        task.execute();
    }
}
