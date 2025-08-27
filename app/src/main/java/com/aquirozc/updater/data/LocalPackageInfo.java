package com.aquirozc.updater.data;

public class LocalPackageInfo{

    public final String name;
    public final String size;
    public final String version;
    public final String date;

    public LocalPackageInfo(String name, String size, String version, String date){
        this.name = name;
        this.size = size;
        this.version = version;
        this.date = date;
    }

    public LocalPackageInfo(String str){
        this(str,str,str,str);
    }


}
