(function( $ ){
	$.fn.extend({
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
		
		getGlobalAlertContainer: function() {
			var form = $(this);
			if (form.find(".alert-container").length > 0) {
				return form.find(".alert-container").first();
			}
			else {
				var alertContainer = $(document.createElement("div")).addClass("alert-container");
				if (form.find(".form-group").length > 0) {
					alertContainer.prependTo(form.find(".form-group").first().closest(".panel-body,.modal-body"));
				} else {
					alertContainer.prependTo(form.find(".panel-body,.modal-body").first());
				}
				return alertContainer;
			}
		},
		
		applicationMessage: function(messageCode) {
			return APPLICATION_MESSAGES[messageCode];
		},
		
		handleResponseStatusError: function(form, xhr) {
			var alertElement = $(document.createElement("div")).displayAlert("error", xhr.status + " - " + $.fn.applicationMessage("exception.generic.message"));
			form.getGlobalAlertContainer().append(alertElement).show();
		},
		
		displayGlobalAlert: function(form, globalAlert) {
			var alertElement = $(document.createElement("div")).displayAlert(globalAlert.type, globalAlert.message);
			form.getGlobalAlertContainer().append(alertElement);
			
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
			
			form.getGlobalAlertContainer().show();
		},
		
		getInputFromFieldError: function(fieldError) {
			return $(this).find("[name='" + fieldError.field + "']");
		},
		
		displayFieldError: function(form, item) {
			var input = form.getInputFromFieldError(item);
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
		},
		
		formBindClearErrors: function() {
			this.each(function() {
				var form = $(this);
				form.find('.form-group').removeClass('has-error').removeClass('has-warning');
				form.find('.help-block').hide();
				form.find('.error-picto').remove();
				form.find('.warning-picto').remove();
				form.find('.alert-container .alert').not('.keep').remove();
				form.find(".form-group").first().closest(".panel-body, .modal-body").find('.alert').not('.keep').remove();
				form.find(".panel-body, .modal-body").find('.alert').not('.keep').remove();

				var globalAlertContainer = form.getGlobalAlertContainer();
				globalAlertContainer.hide();
				globalAlertContainer.find('.alert').remove();
			});
		},
		
		formBindManageJsonErrors: function(xhr) {
			this.each(function() {
				var form = $(this);
				form.trigger("aftersubmit");
				
				if (xhr.status != 200 && xhr.status != 500) {
					$.fn.handleResponseStatusError(form, xhr);
					return;
				}
				
				var response = $.parseJSON(xhr.responseText);
				
				for (var i = 0; i < response.globalAlerts.length; i++) {
					var globalAlert = response.globalAlerts[i];
					$.fn.displayGlobalAlert(form, globalAlert);
				}
				
				for (var i = 0; i < response.fieldErrors.length; i++) {
					var item = response.fieldErrors[i];
					$.fn.displayFieldError(form, item);
				}
			});
		},
		
		linkBindAjaxPost: function(options) {
			$(this).linkTreatment(options);
		},
		
		linkTreatment: function(options){
			this.each(function() {
				
				if(this.tagName != 'FORM'){
					var link = $(this);
					var localOptions = $.extend({
						confirmationMsg : link.attr('data-confirmation-msg'),
						type: 'POST'
					}, options);
					link.click(function() {
						if (localOptions.confirmationMsg != null) {
							if (! confirm(localOptions.confirmationMsg)) {
								return false;
							}
						}
						
						var url = link.attr('href');
						$.ajax(url, {
							success: function(data) {
								if (data.redirection != null) {
									document.location = data.redirection;
								}
								if (options.confirmationMsg) {
									options.onSucces(data);
								}
							},
							
							dataType: "json",
							type: localOptions.type
						});
						return false;
					});
				};
			});
		},
		
		
		formBindAjaxPost: function(options) {
			var defaultTargetRequestPath = function() {
				return location.pathname + location.search + location.hash;
			};
			options = $.extend({
					onSuccess : function(form, data) {
						if (data.redirection != null) {
							location.href = data.redirection;
						} else {
							// Par defaut, on recharge la page en cas de succes
							// Pas de form.trigger("aftersubmit"); car on recharge la page
							location.reload();
						};
					},
				onSuccess : function(form, data) {
					if (data.redirect != null) {
						location.href = data.redirect;
					} else {
						// Par defaut, on recharge la page en cas de succes
						// Pas de form.trigger("aftersubmit"); car on recharge la page
						location.reload();
					};
				},
				
				targetRequestPath : defaultTargetRequestPath,
				
				beforeSend: function(form, request) {
					// le set du header est fait ici car sa valeur peut dependre de valeurs modifiees dans le formulaire
					targetRequestPath = options.targetRequestPath;
					if (typeof targetRequestPath == "function") {
						targetRequestPath = options.targetRequestPath(defaultTargetRequestPath());
					}
					request.setRequestHeader("targetRequestPath", targetRequestPath);
				},
				
				beforeSubmit: function(form, data) {
					// On ne fait rien par défaut
				},
				
				submitOnceOptions: {}
				
			}, options);
			
			this.each(function() {
				if(this.tagName == 'FORM'){
					var form = $(this);
					
					form.submitOnce(options.submitOnceOptions);
					
					form.on("aftersubmit.formBindAjaxPost", function() {
						form.formBindClearErrors();
					});
					form.ajaxForm({
						beforeSend: function (request) {
							options.beforeSend(form, request);
						},
						beforeSubmit: function(data) {
							options.beforeSubmit(form, data);
						},
						success: function(data) {
							options.onSuccess(form, data);
						},
						error: function(xhr) {
							form.formBindManageJsonErrors(xhr);
						}
					});
				};
				
			});
			
			// pour chainage jquery
			return this;
		}
	});
})( jQuery );
