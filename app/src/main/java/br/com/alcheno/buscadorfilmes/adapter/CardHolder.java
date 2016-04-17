package br.com.alcheno.buscadorfilmes.adapter;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import br.com.alcheno.buscadorfilmes.R;

/**
 * Created by lusca on 16/04/2016.
 */
public class CardHolder extends RecyclerView.ViewHolder {

    protected ImageView poster;
    protected TextView descricao, titulo;
    protected ImageView btShare;
    protected CardView card_view;

    public CardHolder(View v) {
        super(v);
        poster =  (ImageView) v.findViewById(R.id.poster);
        descricao = (TextView)  v.findViewById(R.id.descricao);
        titulo = (TextView)  v.findViewById(R.id.titulo);
        btShare = (ImageView)  v.findViewById(R.id.btShare);
        card_view = (CardView) v.findViewById(R.id.card_view);
    }
}
