package eu.se_bastiaan.rslibrary;

/**
 * Sébastiaanmaakt
 * http://sebastiaanmaakt.nl/
 * Date: 12-10-13
 * Time: 19:06
 */
public abstract interface ReadSpeakerCallback {

    public abstract void didFinishReading();
    public abstract void didStartReading();
    public abstract void error(String error);

}