package com.fournineseven.dietstock.ui.ranking;

public class RankingItem {
    int no;
    String name;

    Float kcal;
    Boolean me;

    public RankingItem(int no, String name, Float kcal, Boolean me){

        this.no = no;
        this.name = name;
        this.kcal = kcal;
        this.me = me;
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

    public void setMe(Boolean me) {
        this.me = me;
    }
    public Boolean getMe(){
        return me;
    }
}