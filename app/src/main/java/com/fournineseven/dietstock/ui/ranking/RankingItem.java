package com.fournineseven.dietstock.ui.ranking;

public class RankingItem {
    int no;
    String name;

    Float kcal;

    public RankingItem(int no, String name, Float kcal){

        this.no = no;
        this.name = name;
        this.kcal = kcal;
    }

    public int getNo() {
        return no;
    }

    public void setNo(int no) {
        this.no = no;
    }

    public Float getKcal() {
        return kcal;
    }

    public void setKcal(Float kcal) {
        this.kcal = kcal;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}