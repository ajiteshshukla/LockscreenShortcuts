package com.acubeapps.lockscreen.shortcuts.cards;

import com.acubeapps.lockscreen.shortcuts.analytics.Analytics;
import com.acubeapps.lockscreen.shortcuts.cards.model.MagazineContentItemLayout;
import com.acubeapps.lockscreen.shortcuts.cards.model.MagazineHeaderItemLayout;
import com.inmobi.oem.thrift.ad.model.TContent;
import com.inmobi.oem.thrift.ad.model.TMagazine;
import com.inmobi.oem.thrift.ad.model.TVideo;

import com.felipecsl.asymmetricgridview.AsymmetricItem;
import timber.log.Timber;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;

public final class DemoUtils {

    private DemoUtils() {
    }

    public static List<AsymmetricItem> prepareMagazineItemsAndLayout(TMagazine contents, Analytics analytics) {
        List<AsymmetricItem> items = new ArrayList<>();
        int currentOffset = 0;
        int colSpan = 3;
        int rowSpan = 2;
        //Generate header layout
        MagazineHeaderItemLayout header = new MagazineHeaderItemLayout(colSpan, rowSpan, currentOffset,
                contents.getHeader());
        items.add(header);
        //Generate content layout
        int offset = 1;
        currentOffset = 1;
        for (int i = 0; i < contents.getContentsSize(); i++) {
            if (offset == 1 || offset == 5 || offset == 4) {
                colSpan = 2;
            }  else {
                colSpan = 1;
            }
            if (offset == 3) {
                rowSpan = 2;
            } else {
                rowSpan = 1;
            }
            if (offset == 8) {
                offset = currentOffset + 1;
            } else {
                offset++;
            }
            TContent content = contents.getContents().get(i);
            MagazineContentItemLayout contentItem = new MagazineContentItemLayout(colSpan, rowSpan, currentOffset + i,
                    content);
            items.add(contentItem);
            logContentMeta(content, analytics);
        }
        return items;
    }

    //Logs meta info about content of magazine for analytics
    public static void logContentMeta(TContent content, Analytics analytics) {
        if (content.isSetVideo()) {
            analytics.videoPresent(content.getVideo().getId());
        }
    }

    public static String getVideoEmbedCode(List<TContent> contents) {

        String videoPlayerCode = "";
        String divCode = "";
        int position = 0;

        for (TContent content : contents) {
            if (content.isSetVideo()) {
                TVideo video = content.getVideo();
                if (video.getVideoMetadata().isSetFacebookMetadata()) {
                    String videoId = video.getId();
                    String fbId = videoId;
                    if (video.getVideoMetadata().isSetFacebookMetadata()) {
                        fbId = video.getVideoMetadata().getFacebookMetadata().getId();
                    }

                    long videoDuration = video.getDuration();
                    videoPlayerCode += getVideoPlayerCodeForVideo(videoId, fbId,
                            position, videoDuration);
                    divCode += getDivCodeForVideo(fbId);
                }
            }
            position++;
        }


        String embedCode = getVideoEmbedCode(videoPlayerCode, divCode);
        Timber.i("EmbedCode : " + embedCode);
        return embedCode;
    }

    private static String getVideoEmbedCode(String videoPlayerCode, String divCode) {
        return "<html>\n"
                + " <head>\n"
                + " <style> body {margin:0; padding:0;} </style>"
                + " <title>Your Website Title</title>\n"
                + " <script> "
                + "     var videoPlayers = new Object();"
                + "     window.fbAsyncInit = function() {\n"
                + "         FB.init({\n"
                + "             appId: '161764364221538',\n"
                + "             xfbml: true,\n"
                + "             version: 'v2.5'\n"
                + "         });\n"
                + "         FB.Event.subscribe('xfbml.ready', function(msg) {\n"
                + videoPlayerCode
                + "         });\n"
                + "         };\n"
                + "         (function(d, s, id) {\n"
                + "             var js, fjs = d.getElementsByTagName(s)[0];\n"
                + "             if (d.getElementById(id)) {\n"
                + "                 return;\n"
                + "             }\n"
                + "             js = d.createElement(s);\n"
                + "             js.id = id;\n"
                + "             js.src = \"https://connect.facebook.net/en_US/sdk.js\";\n"
                + "             fjs.parentNode.insertBefore(js, fjs);\n"
                + "         }(document, 'script', 'facebook-jssdk')); \n"
                + getShowDivJsFunction()
                + getHideDivJsFunction()
                + getPlayVideoJsFunction()
                + getPauseVideoJsFunction()
                + getMuteVideoJsFunction()
                + getUnMuteVideoJsFunction()
                + getSeekVideoJsFunction()
                + getCurrentPositionVideoJsFunction()
                + getIsMutedVideoJsFunction()
                + "      </script>  <!-- Your embedded video player code -->  \n"
                + "   </head>\n"
                + "   <body>\n"
                + "      <!-- Load Facebook SDK for JavaScript -->  \n"
                + "      <div id=\"fb-root\"></div>\n"
                + divCode
                + "   </body>\n"
                + "</html>";
    }

    private static String getVideoPlayerCodeForVideo(String videoId, String fbId, int position, long videoDuration) {
        String videoPlayerName = "videoPlayers[\"" + fbId + "\"]";
        return  "               if (msg.type === 'video' && msg.id === 'div_video" + fbId + "') {\n"
                + "                 " + videoPlayerName + " = msg.instance;\n"
                + "                 magazineInterface.xfbmlReady(\"" + fbId + "\");\n"
                + "                 " + videoPlayerName + ".subscribe('finishedBuffering', function(e) {\n"
                + "                     magazineInterface.videoLoaded(\"" + fbId + "\");\n"
                + "                 });\n"
                + "                 " + videoPlayerName + ".subscribe('startedPlaying', function(e) {\n"
                + "                     if (" + videoPlayerName + ".getCurrentPosition() == 0) {\n"
                + "                         magazineInterface.videoStarted(\"" + videoId + "\"," + position + ","
                + videoDuration + ");\n"
                + "                     } else {\n"
                + "                         magazineInterface.videoResumed(\"" + videoId + "\");\n"
                + "                     }\n"
                + "                 });\n"
                + "                 " + videoPlayerName + ".subscribe('finishedPlaying', function(e) {\n"
                + "                     magazineInterface.videoFinished(\"" + videoId + "\");\n"
                + "                 })\n;"
                + "                 " + videoPlayerName + ".subscribe('paused', function(e) {\n"
                + "                     magazineInterface.videoPaused(\"" + videoId + "\");\n"
                + "                 });\n"
                + "             }\n";
    }

    private static String getDivCodeForVideo(String fbId) {
        return "      <div style=\"display:none;\" id=\"div_video" + fbId + "\" class=\"fb-video\" "
                + "data-href=\"https://www.facebook.com/facebook/videos/" + fbId
                + "/\" data-width=\"500\" data-allowfullscreen=\"true\"></div>\n";
    }

    private static String getShowDivJsFunction() {

        return "function showDiv(fbId) {"
                + "     var nodes= document.body.getElementsByTagName('*'), L= nodes.length, temp; "
                + "     while(L) { "
                + "         temp = nodes[--L].id || ''; "
                + "         if(temp.indexOf(\"div_video\")== 0) {"
                + "             document.getElementById(temp).style.display = 'none';"
                + "         }"
                + "     }"
                + "     document.getElementById(\"div_video\" + fbId).style.display = 'block';"
                + "};\n";
    }

    private static String getHideDivJsFunction() {

        return "function hideDivAndUpdateVideoState(fbId) {"
                + "     document.getElementById(\"div_video\" + fbId).style.display = 'none';"
                + "     magazineInterface.updateVideoState(videoPlayers[fbId].isMuted());"
                + "};\n";
    }

    private static String getPlayVideoJsFunction() {
        return "function playVideo(fbId,mute) {"
                + "     videoPlayers[fbId].play();\n"
                + "     if(mute) {\n"
                + "         videoPlayers[fbId].mute();\n"
                + "     } else {\n"
                + "         videoPlayers[fbId].unmute();\n"
                + "     };"
                + "};\n";
    }

    private static String getPauseVideoJsFunction() {
        return "function pauseVideo(fbId) {"
                + "     videoPlayers[fbId].pause();\n"
                + "};\n";
    }

    private static String getMuteVideoJsFunction() {
        return "function muteVideo(fbId) {     videoPlayers[fbId].mute();\n"
                + "};";
    }

    private static String getUnMuteVideoJsFunction() {
        return "function unMuteVideo(fbId) {     videoPlayers[fbId].unmute();\n"
                + "};";
    }

    private static String getSeekVideoJsFunction() {
        return "function seekVideo(fbId, positionInSecs) {     "
                + "videoPlayers[fbId].seek(positionInSecs); };";
    }

    private static String getCurrentPositionVideoJsFunction() {
        return "function getCurrentPosition(fbId) {    "
                + " magazineInterface.currentPosition(videoPlayers[fbId].getCurrentPosition());\n"
                + "};";
    }

    private static String getIsMutedVideoJsFunction() {
        return "function isMuted(fbId) {     "
                + "magazineInterface.isMuted(videoPlayers[fbId].isMuted());\n"
                + "};";
    }

    private static final NavigableMap<Long, String> SUFFIXES = new TreeMap<>();

    static {
        SUFFIXES.put(1_000L, "K");
        SUFFIXES.put(1_000_000L, "M");
        SUFFIXES.put(1_000_000_000L, "G");
        SUFFIXES.put(1_000_000_000_000L, "T");
        SUFFIXES.put(1_000_000_000_000_000L, "P");
        SUFFIXES.put(1_000_000_000_000_000_000L, "E");
    }

    public static String format(long value) {
        //Long.MIN_VALUE == -Long.MIN_VALUE so we need an adjustment here
        if (value == Long.MIN_VALUE) {
            return format(Long.MIN_VALUE + 1);
        }
        if (value < 0) {
            return "-" + format(-value);
        }
        if (value < 1000) {
            return Long.toString(value); //deal with easy case
        }

        Map.Entry<Long, String> e = SUFFIXES.floorEntry(value);
        Long divideBy = e.getKey();
        String suffix = e.getValue();

        long truncated = value / (divideBy / 10); //the number part of the output times 10
        boolean hasDecimal = truncated < 100 && (truncated / 10d) != (truncated / 10);
        return hasDecimal ? (truncated / 10d) + suffix : (truncated / 10) + suffix;
    }

    public static String getFormattedTimeRemaining(long timeElapsedInSec, long totalDurationInSec) {
        long timeRemaining = totalDurationInSec - timeElapsedInSec;
        int minutes = (int) ((timeRemaining % 3600) / 60);
        int seconds = (int) (timeRemaining % 60);
        return String.format("%02d:%02d", minutes, seconds);
    }

    public static long getLikesCount(TVideo video) {
        if (video.getVideoMetadata().isSetFacebookMetadata()) {
            return video.getVideoMetadata()
                    .getFacebookMetadata()
                    .getLikeCount();
        }
        if (video.getVideoMetadata().isSetHostedMetadata()) {
            return video.getVideoMetadata()
                    .getHostedMetadata()
                    .getHeartCount();
        }
        return 10000;
    }
}
