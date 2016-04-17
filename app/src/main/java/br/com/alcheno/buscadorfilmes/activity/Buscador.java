package br.com.alcheno.buscadorfilmes.activity;

import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import br.com.alcheno.buscadorfilmes.adapter.CardAdapter;
import br.com.alcheno.buscadorfilmes.task.HttpTask;
import br.com.alcheno.buscadorfilmes.task.IInterface;
import br.com.alcheno.buscadorfilmes.R;
import br.com.alcheno.buscadorfilmes.to.TOFilme;

public class Buscador extends AppCompatActivity {

    private SearchManager searchManager;
    private SearchView searchView;
    private LinearLayout linearBusqueFilme;
    private RelativeLayout resultado;
    private TextView quantidadeResultados;
    private RecyclerView cardList;
    private CardAdapter adapter;
    private List<TOFilme> filmes;
    private List<TOFilme> filmesSobrando = new ArrayList<>();
    private List<TOFilme> filmesUsados = new ArrayList<>();
    private int paginacao = 1;
    private String newText;
    private ImageLoader imageLoader;

    //paginacao
    private boolean loading = true;
    int pastVisiblesItems, visibleItemCount, totalItemCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buscador);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setLogo(R.mipmap.ic_menu_white_24dp);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        linearBusqueFilme = (LinearLayout) findViewById(R.id.linearBusqueFilme);
        resultado = (RelativeLayout) findViewById(R.id.resultado);
        quantidadeResultados = (TextView) findViewById(R.id.quantidadeResultados);
        cardList = (RecyclerView) findViewById(R.id.cardList);

        cardList.setHasFixedSize(true);
        final LinearLayoutManager mLayoutManager = new LinearLayoutManager(Buscador.this);
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        cardList.setLayoutManager(mLayoutManager);

        cardList.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0) {
                    visibleItemCount = mLayoutManager.getChildCount();
                    totalItemCount = mLayoutManager.getItemCount();
                    pastVisiblesItems = mLayoutManager.findFirstVisibleItemPosition();

                    if (loading) {
                        if ((visibleItemCount + pastVisiblesItems) >= totalItemCount) {
                            loading = false;
                            //Do pagination.. i.e. fetch new data
                            buscarFilmes(true);
                        }
                    }
                }
            }
        });

        //imagens
        DisplayImageOptions opts = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisc(true)
//                .displayer(new SimpleBitmapDisplayer())
                .build();
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getApplicationContext())
                .defaultDisplayImageOptions(opts)
                .threadPriority(Thread.MIN_PRIORITY)
                .build();

        imageLoader = ImageLoader.getInstance();
        imageLoader.init(config);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_buscador, menu);

        MenuItem searchMenuItem = menu.findItem(R.id.menu_search);
        searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) searchMenuItem.getActionView();

        searchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            }
        });

        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                getSupportActionBar().setDisplayHomeAsUpEnabled(false);
                return false;
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(final String newText) {
                setNewText(newText);
                buscarFilmes(false);
                return false;
            }
        });

        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (searchView.isShown()) {
            if (id == android.R.id.home) {
                getSupportActionBar().setDisplayHomeAsUpEnabled(false);
                searchView.onActionViewCollapsed();
            }
        }

        return super.onOptionsItemSelected(item);
    }

    public void buscarFilmes(final boolean scroll) {
        IInterface service = new IInterface() {
            @Override
            public void processFinish(Object output) {

            }

            @Override
            public void processFinish(Object output, int quantidade) {
                JsonParser jsonParser = new JsonParser();
                JsonArray gsonArray = jsonParser.parse(output.toString()).getAsJsonArray();

                Type listOfTestObject = new TypeToken<List<TOFilme>>() {
                }.getType();
                Gson gson = new Gson();
                filmes = new ArrayList<>();
                filmes = gson.fromJson(gsonArray, listOfTestObject);

                //ADD NOS CARDS
                linearBusqueFilme.setVisibility(View.GONE);
                resultado.setVisibility(View.VISIBLE);
                quantidadeResultados.setText(getString(R.string.encontramos) + " " + quantidade + " " + getString(R.string.resultados));

                filmesSobrando.addAll(filmes);

                for (int i = 0; i < 6; i++) {
                    filmesUsados.add(filmesSobrando.get(0));
                    filmesSobrando.remove(0);
                }

                if (!scroll) {
                    adapter = new CardAdapter(filmesUsados, Buscador.this, imageLoader);
                    cardList.setAdapter(adapter);
                } else {
                    adapter.notifyDataSetChanged();
                }

                paginacao++;
                loading = true;
            }

            @Override
            public void processFinishErro(String output) {
//                        Toast.makeText(Buscador.this, output, Toast.LENGTH_LONG).show();
            }
        };
        HttpTask task = new HttpTask(service, getString(R.string.link_filmes_disponiveis), getNewText(), "s", true, paginacao);
        task.execute();
    }

    public String getNewText() {
        return newText;
    }

    public void setNewText(String newText) {
        this.newText = newText;
    }
}
