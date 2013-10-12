package eu.se_bastiaan.rslibrary;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.PowerManager;
import android.util.Log;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

/**
 * Sébastiaanmaakt
 * http://sebastiaanmaakt.nl/
 * Date: 12-10-13
 * Time: 19:06
 */
public final class ReadSpeaker {

    private final String LOG_TAG = getClass().getSimpleName(), STAT_TYPE = "AndroidLibrary";;
    private Context mContext;
    private ReadSpeakerCallback mCallback;
    private Integer mCustomerId;
    private String mCustomerUrl;
    private Boolean mWakeLockEnabled, mReadDirectly, mStopRequested;
    private PowerManager.WakeLock mWakeLock;
    private MediaPlayer mMediaPlayer;

    /**
     * Preconfigured languages
     */
    public static final String
            ARABIC = "ar_ar",
            BASQUE = "eu_es",
            CATALAN = "ca_es",
            CHINESE_CANTONESE = "zh_hk",
            CHINESE_MANDARIN = "zh_cn",
            CZECH = "cs_cz",
            DANISH = "da_dk",
            DUTCH = "nl_nl",
            ENGLISH_UK = "en_uk",
            ENGLISH_US = "en_us",
            ENGLISH_AU = "en_au",
            ENGLISH_IN = "en_in",
            ENGLISH_SC = "en_sc",
            ENGLISH_ZA = "en_za",
            FAROESE = "fo_fo",
            FINNISH = "fi_fi",
            FLEMISH = "nl_be",
            FRENCH = "fr_fr",
            FRISIAN = "fy_nl",
            GALICIAN = "gl_es",
            GERMAN = "de_de",
            GREEK = "el_gr",
            HINDI = "hi_in",
            ITALIAN = "it_it",
            JAPANESE = "ja_jp",
            KOREAN = "ko_kr",
            NORWEGIAN_BOKMAL = "no_nb",
            NORWEGIAN_NYNORSK = "no_nn",
            POLISH = "pl_pl",
            PORTUGUESE_PT = "pt_pt",
            PORTUGUESE_BR = "pt_br",
            ROMANIAN = "ro_ro",
            RUSSIAN = "ru_ru",
            SPANISH_ES = "es_es",
            SPANISH_US = "es_us",
            SWEDISH = "sv_se",
            TURKISH = "tr_tr",
            VALENCIAN = "ca_es",
            WELSH = "cy_cy";

    /**
     * ReadSpeaker constructor
     * @param context Activity or Service
     * @param wakeLock Enable wakelock
     * @param callback ReadSpeakerCallback
     */
    public ReadSpeaker(Context context, Boolean wakeLock, ReadSpeakerCallback callback) {
        init(context, wakeLock, callback, null, null);
    }

    /**
     *
     * @param context Activity or Service
     * @param wakeLock Enable wakelock
     */
    public ReadSpeaker(Context context, Boolean wakeLock) {
        init(context, wakeLock, null, null, null);
    }

    /**
     *
     * @param context Activity or Service
     * @param wakeLock Enable wakelock
     * @param callback ReadSpeakerCallback
     * @param customerId ReadSpeaker enterpriseID
     * @param customerUrl ReadSpeaker Website URL
     */
     public ReadSpeaker(Context context, Boolean wakeLock, ReadSpeakerCallback callback, Integer customerId, String customerUrl) {
     init(context, wakeLock, callback, customerId, customerUrl);
     }

    /**
     *
     * @param context Activity or Service
     * @param wakeLock Enable wakelock
     * @param customerId ReadSpeaker enterpriseID
     * @param customerUrl ReadSpeaker Website URL
     */
    public ReadSpeaker(Context context, Boolean wakeLock, Integer customerId, String customerUrl) {
        init(context, wakeLock, null, customerId, customerUrl);
    }

    /**
     * Initiate all variables
     * @param context Activity or Service
     * @param wakeLock Enable wakelock
     * @param callback ReadSpeakerCallback
     * @param customerId ReadSpeaker enterpriseID
     * @param customerUrl ReadSpeaker Website URL
     */
    private void init(Context context, Boolean wakeLock, ReadSpeakerCallback callback, Integer customerId, String customerUrl) {
        mContext = context;
        mCallback = callback;
        mCustomerId = customerId;
        mCustomerUrl = customerUrl;
        mMediaPlayer = new MediaPlayer();

        mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                if(!mStopRequested && mCallback != null) mCallback.didFinishReading();
            }
        });

        mMediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                if(!mStopRequested) {
                    stopAudio();
                    if(mCallback != null) mCallback.error("Error during playback, code: " + what + "(" + extra + ")");
                }
                return true;
            }
        });

        mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                if(!mStopRequested) mp.start();
                if(mCallback != null) mCallback.didStartReading();
            }
        });

        setWakeLockEnabled(wakeLock);
    }

    /**
     * Start wakelock
     */
    private void initWakeLock() {
        if(mWakeLockEnabled) {
            PowerManager powerManager = (PowerManager) mContext.getSystemService("power");
            mWakeLock = powerManager.newWakeLock(PowerManager.ON_AFTER_RELEASE, "ReadSpeakerLibrary");
            mWakeLock.setReferenceCounted(false);
        }
    }

    /**
     * Enable or disable wakelock
     * @param enabled Enable wakelock
     */
    public void setWakeLockEnabled(Boolean enabled) {
        mWakeLockEnabled = enabled;
        if(mWakeLockEnabled && mWakeLock == null) {
            initWakeLock();
        } else if(!mWakeLockEnabled && mWakeLock != null) {
            mWakeLock.release();
            mWakeLock = null;
        }
    }

    /**
     * Connects to the ReadSpeaker server to generate a audiofile for playback
     * @param text Text that has to be read
     * @param html Text is HTML or not
     * @param cache Do we need to cache your text or not
     * @param lang In what language is your text
     */
    public void obtainAudioLocation(String text, boolean html, boolean cache, String lang) {
        obtainAudioLocation(text, html, cache, lang, null);
    }

    /**
     * Connects to the ReadSpeaker server to generate a audiofile for playback
     * @param text Text that has to be read
     * @param html Text is HTML or not
     * @param cache Do we need to cache your text or not
     * @param lang In which language is your text
     * @param voice The voice you want to use
     */
    public void obtainAudioLocation(String text, boolean html, boolean cache, String lang, String voice) {
        String returnString = "";

        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("output", "audiolink"));
        if (html) {
            if (cache)
                params.add(new BasicNameValuePair("html", text));
            else {
                params.add(new BasicNameValuePair("selectedhtml", text));
            }
        } else if (cache) {
            params.add(new BasicNameValuePair("text", text));
        } else {
            params.add(new BasicNameValuePair("selectedtext", text));
        }
        params.add(new BasicNameValuePair("customerid", Integer.toString(mCustomerId)));
        params.add(new BasicNameValuePair("lang", lang));
        params.add(new BasicNameValuePair("url", mCustomerUrl));
        if(voice != null) params.add(new BasicNameValuePair("voice", voice));
        params.add(new BasicNameValuePair("stattype", STAT_TYPE));

        new AsyncTask<List<NameValuePair>, Void, String>() {

            @Override
            protected String doInBackground(List<NameValuePair>... givenParams) {
                String returnString = "";
                try {
                    URL serverUrl = new URL("http://app.readspeaker.com/cgi-bin/rsent");
                    List<NameValuePair> params = givenParams[0];

                    StringBuilder parametersAsQueryString = new StringBuilder();
                    boolean firstParameter = true;

                    for (NameValuePair parameter : params) {
                        if (!firstParameter) {
                            parametersAsQueryString.append("&");
                        }

                        try {
                            parametersAsQueryString.append(parameter.getName())
                                    .append(URLEncoder.encode("=", "UTF-8"))
                                    .append(URLEncoder.encode(parameter.getValue(), "UTF-8"));
                        } catch(UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }

                        firstParameter = false;
                    }

                    String paramsString = parametersAsQueryString.toString();

                    Log.d(LOG_TAG, paramsString);

                    HttpURLConnection urlConnection = (HttpURLConnection) serverUrl.openConnection();
                    urlConnection.setDoInput(true);
                    urlConnection.setDoOutput(true);
                    urlConnection.setInstanceFollowRedirects(true);
                    urlConnection.setRequestMethod("POST");
                    urlConnection.setRequestProperty("Content-Encoding", "application/x-www-form-urlencoded");
                    urlConnection.setRequestProperty("Referer", mCustomerUrl);
                    urlConnection.setReadTimeout(30000);

                    OutputStream outputStream = urlConnection.getOutputStream();
                    outputStream.write(paramsString.getBytes());
                    outputStream.flush();
                    outputStream.close();

                    InputStream inputStream = urlConnection.getInputStream();
                    InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
                    StringBuilder sb = new StringBuilder();
                    int c;
                    while((c = inputStreamReader.read()) != -1) {
                        sb.append((char) c);
                    }
                    returnString = sb.toString();
                    Log.d(LOG_TAG, returnString);
                } catch (Exception e) {
                    e.printStackTrace();
                    if(mCallback != null) mCallback.error(e.getMessage());
                }
                return returnString;
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                if(mCallback != null && mCallback instanceof ExtendedReadSpeakerCallback) ((ExtendedReadSpeakerCallback) mCallback).obtainedAudioLocation(s);
                if(mReadDirectly) playAudio(s);
            }
        }.execute(params);
    }

    /**
     * Connects to the ReadSpeaker server to generate a audiofile for playback and plays directly
     * @param text Text that has to be read
     * @param html Text is HTML or not
     * @param cache Do we need to cache your text or not
     * @param lang In which language is your text
     * @param voice The voice you want to use
     */
    public void read(String text, boolean html, boolean cache, String lang, String voice) {
        mReadDirectly = true;
        obtainAudioLocation(text, html, cache, lang, voice);
    }

    /**
     * Connects to the ReadSpeaker server to generate a audiofile for playback and plays directly
     * @param text Text that has to be read
     * @param html Text is HTML or not
     * @param cache Do we need to cache your text or not
     * @param lang In which language is your text
     */
    public void read(String text, boolean html, boolean cache, String lang) {
        read(text, html, cache, lang, null);
    }

    /**
     * Play audio from url
     * @param url Location of audiofile
     */
    public void playAudio(final String url) {
        mReadDirectly = false;

        try {
            if(mWakeLock != null) mWakeLock.acquire(600000);
            stopAudio(true);
            mMediaPlayer.reset();
            mMediaPlayer.setDataSource(url);
            mMediaPlayer.prepare();
        } catch(IOException e) {
            if(mCallback != null) mCallback.error(e.getMessage());
        }
    }

    /**
     * Stop audio
     */
    public void stopAudio() {
        stopAudio(false);
    }

    /**
     * Stop audio
     * @param shouldStartPlayingDirectlyAfterwards private
     */
    private void stopAudio(boolean shouldStartPlayingDirectlyAfterwards) {
        mStopRequested = (!shouldStartPlayingDirectlyAfterwards);

        if (mWakeLock != null) mWakeLock.release();
        if (mMediaPlayer.isPlaying()) mMediaPlayer.stop();
    }

}
