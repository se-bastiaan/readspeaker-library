ReadSpeakerLibrary
==================

Alternative Android library for ReadSpeaker AudioMobile (www.readspeaker.com)

Getting started
----------------

Using the library is easy.

```Java
public class SampleActivity extends Activity {
  
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_testprepare);
    
    // New ReadSpeaker(), wakelock disabled, no callback
    ReadSpeaker speaker = new ReadSpeaker(this, false, myEnterpriseId, myCustomerUrl);
    speaker.read("This is a test", false, false, ReadSpeaker.ENGLISH_UK);
  }
  
}
```

Do you want to check for any events? Use the callback interface.

```Java
public class SampleActivity extends Activity implements ReadSpeakerCallback {
  
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_testprepare);
    
    // New ReadSpeaker(), wakelock disabled, callback set
    ReadSpeaker speaker = new ReadSpeaker(this, false, this, myEnterpriseId, myCustomerUrl);
    speaker.read("This is a test", false, false, ReadSpeaker.ENGLISH_UK);
  }
  
  @Override
  public void didFinishReading() {
      // Finished reading
  }

  @Override
  public void didStartReading() {
      // Just started reading the text
  }

  @Override
  public void error(String s) {
      // An error occurred!
  }
  
}
```

The library can use Android's WakeLock functions to make sure the device will stay awake while reading a text. Your application needs to have the right permissions to use this functionality!

```Java
public class SampleActivity extends Activity implements ReadSpeakerCallback {
  
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_testprepare);
    
    // New ReadSpeaker(), wakelock enbled, callback set
    ReadSpeaker speaker = new ReadSpeaker(this, true, this, myEnterpriseId, myCustomerUrl);
    speaker.read("This is a test", false, false, ReadSpeaker.ENGLISH_UK);
  }
  
  @Override
  public void didFinishReading() {
      // Finished reading
  }

  @Override
  public void didStartReading() {
      // Just started reading the text
  }

  @Override
  public void error(String s) {
      // An error occurred!
  }
  
}
```

or use

```
speaker.setWakeLockEnabled(true);
```

Preconfigured languages
-----------------------

I've put all languages I can find in the library so it's easy to select the one you need. Languages that aren't in the library can still be used, just use the right language code in read(). Make sure your account is able to use the language you selected. 

```
  ARABIC = "ar_ar"
  BASQUE = "eu_es"
  CATALAN = "ca_es"
  CHINESE_CANTONESE = "zh_hk"
  CHINESE_MANDARIN = "zh_cn"
  CZECH = "cs_cz"
  DANISH = "da_dk"
  DUTCH = "nl_nl"
  ENGLISH_UK = "en_uk"
  ENGLISH_US = "en_us"
  ENGLISH_AU = "en_au"
  ENGLISH_IN = "en_in"
  ENGLISH_SC = "en_sc"
  ENGLISH_ZA = "en_za"
  FINNISH = "fi_fi"
  FLEMISH = "nl_be"
  FRENCH = "fr_fr"
  FRISIAN = "fy_nl"
  GALICIAN = "gl_es"
  GERMAN = "de_de"
  GREEK = "el_gr"
  HINDI = "hi_in"
  ITALIAN = "it_it"
  JAPANESE = "ja_jp"
  KOREAN = "ko_kr"
  NORWEGIAN_BOKMAL = "no_nb"
  NORWEGIAN_NYNORSK = "no_nn"
  POLISH = "pl_pl"
  PORTUGUESE_PT = "pt_pt"
  PORTUGUESE_BR = "pt_br"
  ROMANIAN = "ro_ro"
  RUSSIAN = "ru_ru"
  SPANISH_ES = "es_es"
  SPANISH_US = "es_us"
  SWEDISH = "sv_se"
  TURKISH = "tr_tr"
  VALENCIAN = "ca_es"
  WELSH = "cy_cy
```
