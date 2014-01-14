(function( $ ){
	$.extend($.fn, {
		displayAlert: function(type, message) {
			this.each(function() {
				var element = $(this);
				element.addClass("alert");
				var style = "alert-" + type;
				if (type == "error") {
					style = "alert-danger";
				}
				element.addClass(style);
				element.append('<a class="close" href="#" data-dismiss="alert">&times;</a><p><strong>' + message + '</strong></p>');
			});
			// chainage jquery
			return this;
		},
		
		formBindClearErrors: function() {
			this.each(function() {
				var form = $(this);
				form.find('.form-group').removeClass('has-error').removeClass('has-warning');
				form.find('.help-block').hide();
				form.find('.error-picto').remove();
				form.find('.warning-picto').remove();
				form.find(".form-group").first().closest(".panel-body, .modal-body").find('.alert').not('.keep').remove();
				form.find(".panel-body, .modal-body").find('.alert').not('.keep').remove();

				var globalAlertContainer = $(".global-alerts");
				globalAlertContainer.hide();
				globalAlertContainer.find('.alert').remove();
			});
		},
		
		formBindManageJsonErrors : function(xhr) {
			this.each(function() {
				var form = $(this);

				var displayAlert = function(alertElement) {
					if (form.find(".form-group").length > 0) {
						form.find(".form-group").first().closest(".panel-body,.modal-body").prepend(alertElement);
					} else {
						form.find(".panel-body,.modal-body").first().prepend(alertElement);
					}
				};
				
				if (xhr.status != 200 && xhr.status != 500) {
					var alertElement = $(document.createElement("div")).displayAlert("error", xhr.status + " - " + APPLICATION_MESSAGES["exception.generic.message"]);
					displayAlert(alertElement);
					return;
				}
				
				var response = $.parseJSON(xhr.responseText);
				
				for (var i = 0; i < response.globalAlerts.length; i++) {
					var globalAlert = response.globalAlerts[i];
					var alertElement = $(document.createElement("div")).displayAlert(globalAlert.type, globalAlert.message);
					displayAlert(alertElement);
					
					if (globalAlert.details) {
						// popover en dessous de l'Alert
						alertElement.addClass("display-popover");
						alertElement.attr("title", "Exception");
						alertElement.data("content", globalAlert.details);
						alertElement.popover({
							animation: false,
							placement: "bottom",
							trigger: "click",
							beforeShow: function(popover) {
								popover.addClass("popover-exception");
							},
							closable: true
						});
					}
				}
				
				for (var i = 0; i < response.fieldErrors.length; i++) {
					var item = response.fieldErrors[i];
					
					var input = form.find("[name='" + item.field + "']");
					var formGroup = input.closest(".form-group");
					formGroup.addClass("has-warning");
					
					var errorPicto = $(document.createElement("span")).html("!").addClass("warning-picto");
					formGroup.find("label.control-label").prepend(errorPicto);
					
					var helpBlock = formGroup.find(".help-block");
					if (helpBlock.length == 0) {
						// Ajout du conteneur de message d'erreur
						helpBlock = $(document.createElement("span")).addClass("help-block").insertAfter(input);
					}
					helpBlock.html(item.message).show();
				}
			});
		},
		
		formBindAjaxPost: function(options) {
			var defaultTargetRequestPath = function() {
				return location.pathname + location.search + location.hash;
			};
			options = $.extend({
					onSuccess : function(form, html) {
						// Par defaut, on recharge la page en cas de succes
						location.reload();
					},
					targetRequestPath : defaultTargetRequestPath
				}, options);
			
			this.each(function() {
				var form = $(this);
				form.submitOnce();
				
				form.on("aftersubmit.formBindAjaxPost", function() {
					form.formBindClearErrors();
				});
				
				form.ajaxForm({
					beforeSend: function (request) {
						// le set du header est fait ici car sa valeur peut dependre de valeurs modifiees dans le formulaire
						targetRequestPath = options.targetRequestPath;
						if (typeof targetRequestPath == "function") {
							targetRequestPath = options.targetRequestPath(defaultTargetRequestPath());
						}
						request.setRequestHeader("targetRequestPath", targetRequestPath);
					},
					beforeSubmit: function(data) {
						// $(window).trigger("showloading");
					},
					success: function(data) {
						form.trigger("aftersubmit");
						options.onSuccess(form, data);
					},
					error: function(xhr) {
						// $(window).trigger("hideloading");
						form.trigger("aftersubmit");
						
						form.formBindManageJsonErrors(xhr);
					}
				});
			});
			// pour chainage jquery
			return this;
		}
	});
})( jQuery );