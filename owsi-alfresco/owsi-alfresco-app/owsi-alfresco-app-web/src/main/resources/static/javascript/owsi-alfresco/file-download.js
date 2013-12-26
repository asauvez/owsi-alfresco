/**
 * Dépendence : jquery.cookies.2.2.0.min.js
 */
(function( $ ){
	$.extend($.fn, {
		fileDownload: function() {
			$(this).each(function() {
				var me = $(this);
				
				var url = me.attr("href");
				if (url == undefined) {
					url = me.data("href");
				}
				
				var downloadFile = function(url) {
					$(window).trigger("showloading");
					
					var salt = Math.floor(Math.random() * 2000000);
					var path = location.pathname;
					var slashIndex = path.lastIndexOf("/");
					if (slashIndex != -1) {
						path = path.substring(0, slashIndex);
					}
					
					var form = $('<form action="' + url + '" method="GET">' +
							'<input type="hidden" name="fileDownloadToken" value="' + salt + '"/>' +
							'<input type="hidden" name="fileDownloadPath" value="' + path + '"/>' +
							'</form>'
					);
					$("body").append(form);
					form.submit();
					
					var timerId = setInterval(function() {
						var value = $.cookie("fileDownloadToken");
						if (value && parseInt(value) == salt) {
							clearInterval(timerId);
							$.removeCookie("fileDownloadToken", { path: path });
							$(window).trigger("hideloading");
							return;
						}
					}, 500); // makes this code execute every 500ms
				};
				
				me.on("click", function(event) {
					event.preventDefault();
					downloadFile(url);
				});
			});
			// chainage jquery
			return this;
		}
	});
})( jQuery );