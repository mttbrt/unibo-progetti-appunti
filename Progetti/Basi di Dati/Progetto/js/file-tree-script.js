if(jQuery) (function($) {
	$.extend($.fn, {
		fileTree: function(o, h) {
			// Defaults
			if( !o ) var o = {};
			if( o.root == undefined ) o.root = '/'; // root folder to display; default = /
			if( o.script == undefined ) o.script = 'system/controller.php'; // location of the serverside AJAX file to use; default = system/controller.php
			if( o.folderEvent == undefined ) o.folderEvent = 'click'; // event to trigger expand/collapse; default = click
			if( o.expandSpeed == undefined ) o.expandSpeed= 300; // default = 300 (ms); use -1 for no animation
			if( o.collapseSpeed == undefined ) o.collapseSpeed= 300; // default = 300 (ms); use -1 for no animation
			if( o.expandEasing == undefined ) o.expandEasing = null; // easing function to use on expand (optional)
			if( o.collapseEasing == undefined ) o.collapseEasing = null; // easing function to use on collapse (optional)
			if( o.multiFolder == undefined ) o.multiFolder = true; // whether or not to limit the browser to one subfolder at a time
			if( o.loadMessage == undefined ) o.loadMessage = 'Loading...'; // Message to display while initial tree loads (can be HTML)

			$(this).each(function() {
				function showTree(c, t) {
					$(c).addClass('wait');
					$(".fileTreeBody.start").remove();
					$.post(o.script, {"op": 0, "path": t}, function(data) {
						$(c).find('.start').html('');
						$(c).removeClass('wait').append(data);
						if( o.root == t )
							$(c).find('ul:hidden').show();
						else
							$(c).find('ul:hidden').slideDown({ duration: o.expandSpeed, easing: o.expandEasing });
						bindTree(c);
					});
				}

				function bindTree(t) {
					$(t).find('li a').bind(o.folderEvent, function() {
						if( $(this).parent().hasClass('directory') ) {
							if( $(this).parent().hasClass('collapsed') ) {
								// Expand
								if( !o.multiFolder ) {
									$(this).parent().parent().find('ul').slideUp({ duration: o.collapseSpeed, easing: o.collapseEasing });
									$(this).parent().parent().find('li.directory').removeClass('expanded').addClass('collapsed');
								}
								$(this).parent().find('ul').remove(); // cleanup
								showTree( $(this).parent(), escape($(this).attr('rel').match( /.*\// )) );
								$(this).parent().removeClass('collapsed').addClass('expanded');
							} else {
								// Collapse
								$(this).parent().find('ul').slideUp({ duration: o.collapseSpeed, easing: o.collapseEasing });
								$(this).parent().removeClass('expanded').addClass('collapsed');
							}
						} else {
							h($(this).attr('rel'));
						}
						return false;
					});

					// Prevent a from triggering the # on non-click events
					if( o.folderEvent.toLowerCase != 'click' ) $(t).find('li a').bind('click', function() { return false; });
				}
				// Loading message
				$(this).html('<ul class="fileTreeBody start"><li class="wait">' + o.loadMessage + '<li></ul>');
				// Get the initial file list
				showTree( $(this), escape(o.root) );
			});
		}
	});
})(jQuery);
