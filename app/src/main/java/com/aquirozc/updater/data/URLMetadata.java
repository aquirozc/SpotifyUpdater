package com.aquirozc.updater.data;

public enum URLMetadata {

    ANDROFOREVER_METADA("androforever.com","%20",2),
    APKMODY_METADATA("apkmody.com","_",1);

    public final String domain;
    public final String separator;
    public final int index;

    URLMetadata(String domain, String separator, int index){
        this.domain = domain;
        this.separator = separator;
        this.index = index;
    }

}
