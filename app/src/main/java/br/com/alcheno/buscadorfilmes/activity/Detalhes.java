package br.com.alcheno.buscadorfilmes.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;

import br.com.alcheno.buscadorfilmes.R;
import br.com.alcheno.buscadorfilmes.to.TOFilme;
import br.com.alcheno.buscadorfilmes.to.TOFilmeCompleto;

/**
 * Created by lusca on 16/04/2016.
 */
public class Detalhes extends AppCompatActivity {

    private TextView titulo, descricao, atores, video, escritores, data, duracao, tipo, black_star, premios, local;
    private ImageView poster, btBack, btShare;
    private TOFilmeCompleto filme;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalhes);

        titulo = (TextView) findViewById(R.id.titulo);
        descricao = (TextView) findViewById(R.id.descricao);
        atores = (TextView) findViewById(R.id.atores);
        video = (TextView) findViewById(R.id.video);
        escritores = (TextView) findViewById(R.id.escritores);
        data = (TextView) findViewById(R.id.data);
        duracao = (TextView) findViewById(R.id.duracao);
        tipo = (TextView) findViewById(R.id.tipo);
        black_star = (TextView) findViewById(R.id.black_star);
        premios = (TextView) findViewById(R.id.premios);
        local = (TextView) findViewById(R.id.local);
        poster = (ImageView) findViewById(R.id.poster);
        btBack = (ImageView) findViewById(R.id.btBack);
        btShare = (ImageView) findViewById(R.id.btShare);

        if (getIntent().getExtras() != null) {
            Bundle b = getIntent().getExtras();
            filme = (TOFilmeCompleto) b.getSerializable("filme");

            if (filme != null) {
                titulo.setText(filme.getTitle());
                descricao.setText(filme.getPlot());
                atores.setText(filme.getActors());
                video.setText(filme.getDirector());
                escritores.setText(filme.getWriter());
                data.setText(filme.getReleased());
                duracao.setText(filme.getRuntime());
                tipo.setText(filme.getGenre());
                black_star.setText(filme.getMetascore());
                premios.setText(filme.getAwards());
                local.setText(filme.getCountry());
            }
        }

        //share
        btShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                sharingIntent.putExtra(Intent.EXTRA_SUBJECT, filme.getTitle());
                sharingIntent.putExtra(Intent.EXTRA_TEXT, filme.getPlot());
                sharingIntent.setType("image/*");
                sharingIntent.putExtra(Intent.EXTRA_STREAM, filme.getPoster());
                startActivity(Intent.createChooser(sharingIntent, "Share via"));
            }
        });

        //back
        btBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        //poster
        DisplayImageOptions opts = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisc(true)
                .displayer(new SimpleBitmapDisplayer())
                .build();
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this).defaultDisplayImageOptions(opts).build();
        ImageLoader imageLoader = ImageLoader.getInstance();
        imageLoader.init(config);
        imageLoader.displayImage(filme.getPoster(), poster);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(R.anim.slide_center_right_exit, R.anim.slide_center_left_exit);
    }
}
