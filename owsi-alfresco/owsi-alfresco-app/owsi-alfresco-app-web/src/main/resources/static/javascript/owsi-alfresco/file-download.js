/**
 * Dépendence : js-cookie
 */
(function( $ ){
	$.fn.extend({
		fileDownload: function(options) {
			
			options = $.extend({
				cookieTimerInterval: 500,
				cookieName: "fileDownloadToken",
				tokenInput: "fileDownloadToken",
				pathInput: "fileDownloadPath"
			}, options);
			
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
					
					var formS = '<form action="' + url + '" method="GET">' +
							'<input type="hidden" name="' + options.tokenInput + '" value="' + salt + '"/>' +
							'<input type="hidden" name="' + options.pathInput + '" value="' + path + '"/>';
					
					var hashes = url.slice(url.indexOf('?') + 1).split('&');
					for(var i = 0; i < hashes.length; i++) {
						var hash = hashes[i].split('=');
						formS += '<input type="hidden" name="' + hash[0] + '" value="' + hash[1] + '"/>'
					}
					formS += '</form>';
					var form = $(formS);
					$("body").append(form);
					form.submit();
					
					var timerId = setInterval(function() {
//						var value = $.cookie(options.cookieName);
//						if (value && parseInt(value) == salt) {
//							clearInterval(timerId);
//							$.removeCookie(options.cookieName, { path: path });
//							$(window).trigger("hideloading");
//							return;
//						}
						var value = Cookies.get(options.cookieName);
						if (value && parseInt(value) == salt) {
							clearInterval(timerId);
							Cookies.remove(options.cookieName, { path: path });
							$(window).trigger("hideloading");
							return;
						}
					}, options.cookieTimerInterval); // makes this code execute every "options.cookieTimerInterval" ms
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