
//[START] Intercept Javascript
if (!__intercept_initialize) {
    function __intercept_initialize() {
        var controlsRemoved = false;
        var detectionPeriod = 100;

        function removeControls() {
            if (controlsRemoved) {
                return;
            }

            var path = location.pathname;
            if (!path.match(/\/video\/embed/g)) {
                return;
            }

            var videos = document.getElementsByTagName("VIDEO");
            if (videos != null && videos.length > 0) {
                for (var i = 0; i < videos.length; i++) {
                    var v = videos[i];
                    v.removeAttribute("controls");
                    controlsRemoved = true;
                    var src = v.getAttribute("src");
                    __intercept_videoApi.onVideo(__intercept_videoId, src);
                }
            }

            window.setTimeout(removeControls, detectionPeriod);
        }

        window.addEventListener('load', function() {
            window.setTimeout(removeControls, detectionPeriod);
        });
    }

    __intercept_initialize();
}
//[END] Intercept Javascript
