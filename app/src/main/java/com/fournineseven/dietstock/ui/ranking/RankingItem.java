package com.fournineseven.dietstock.ui.ranking;

public class RankingItem {
    int no;
    String name;
    String kcal;
    Boolean isUp;

    public RankingItem(int no, String name, String kcal, Boolean isUp){
        this.no = no;
        this.name = name;
        this.kcal = kcal;
        this.isUp = isUp;
    }

    public int getNo() {
        return no;
    }

    public void setNo(int no) {
        this.no = no;
    }

    public Boolean getUp() {
        return isUp;
    }

    public void setUp(Boolean up) {
        isUp = up;
    }

    public String getKcal() {
        return kcal;
    }

    public void setKcal(String kcal) {
        this.kcal = kcal;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
