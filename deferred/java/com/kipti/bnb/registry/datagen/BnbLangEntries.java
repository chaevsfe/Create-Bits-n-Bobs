package com.kipti.bnb.registry.datagen;

import com.kipti.bnb.azimuth.foundation.lang.AzimuthGeneratedLangEntry;
import com.kipti.bnb.azimuth.lang.LangDefaultCollector;
import com.kipti.bnb.CreateBitsnBobs;

public class BnbLangEntries {

    public static void register() {
        LangDefaultCollector.collectAll();
        AzimuthGeneratedLangEntry.provideLang(CreateBitsnBobs.MOD_ID, CreateBitsnBobs.REGISTRATE::addRawLang);
    }

}

