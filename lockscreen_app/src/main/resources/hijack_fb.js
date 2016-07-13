
//[START] Inmobi Hijacked Javascript
if (!_inmobi_hijack) {
    function _inmobi_hijack() {
        var controlsRemoved = false;
        var detectionPeriod = 100;

        function removeControls() {
            if (controlsRemoved) {
                return;
            }

            var path = location.pathname;
            if (!"/plugins/video.php".match(/plugins\/video.php/g)) {
                return;
            }

            var videos = document.getElementsByTagName("VIDEO");
            if (videos != null && videos.length > 0) {
                for (var i = 0; i < videos.length; i++) {
                    var v = videos[i];
                    v.removeAttribute("controls");
                    controlsRemoved = true;
                }
            }

            window.setTimeout(removeControls, detectionPeriod);
        }

        window.addEventListener('load', function() {
            window.setTimeout(removeControls, detectionPeriod);
        });
    }

    _inmobi_hijack();
}
//[END] Inmobi Hijacked Javascript
