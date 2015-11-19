<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="stripes" uri="http://stripes.sourceforge.net/stripes.tld" %>
<stripes:layout-render name="layout/adminlayout.jsp">
    <stripes:layout-component name="contents">
		
	<div id="header"	class="hidden">
		<div  class="nav-back-color border_accent" style="width: 100%">
			<table style="width: 100%; border-collapse: collapse;" >
				<tr>					
					<td style="width: 33%">
						<div class="logo-small" style="    display: inline-block;">
							<img src="../images/logo/logo-only-words.svg" alt="" onclick="window.location.replace('../');" title="Go back to Home Page" style="height: 49px; margin: 3px 15px 0px 15px">
						</div>						
					</td>
					<td align="center" style="width: 33%">
						<span style="text-align: center; width: 100%; font-size: 35px; color: white;">
							Admin Tools
						</span>
					</td>
					<td style="width: 33%">
						<div id="userMenu" style="float: right; padding-right: 10px;">						
						</div>						
					</td>
				</tr>
			</table>
		</div>	
	</div>			

		
	<script type="text/javascript">
		/* global Ext, CoreService, CoreApp */	
		Ext.onReady(function(){
			
			
			var contents = Ext.create('OSF.ux.IFrame', {
				src: ''
			});
			
			var contentPanel = Ext.create('Ext.panel.Panel', {
				frame: true,
				layout: 'fit',
				items: [
					contents
				]
			});
			
			var pageMap = [];
			pageMap['Attributes'] = '/openstorefront/admin?tool=Attributes';
			pageMap['Dashboard'] = 'Router.action?page=admin/adminDashboard.jsp';
			pageMap['Entries'] = 'Router.action?page=admin/data/components.jsp';//'/openstorefront/admin?tool=Entries';
			pageMap['EntriesOld'] = '/openstorefront/admin?tool=Entries';
			pageMap['EntryType'] = 'Router.action?page=admin/data/entryType.jsp';
			pageMap['Highlights'] = '/openstorefront/admin?tool=Highlights';
			pageMap['Integrations'] = '/openstorefront/admin?tool=Integrations';
			pageMap['Imports'] = '/openstorefront/admin?tool=Imports';
			pageMap['Lookups'] = 'Router.action?page=admin/data/lookup.jsp';
			pageMap['Media'] = '/openstorefront/admin?tool=Media';
			pageMap['Organizations'] = '/openstorefront/admin?tool=Organizations';
			pageMap['Questions'] = '/openstorefront/admin?tool=Questions';
			pageMap['Reviews'] = '/openstorefront/admin?tool=Reviews';
			pageMap['Tags'] = '/openstorefront/admin?tool=Tags';
			pageMap['UserProfiles'] = '/openstorefront/admin?tool=User%20Profiles';
			pageMap['Alerts'] = '/openstorefront/admin?tool=Alerts';
			pageMap['Branding'] = '/openstorefront/admin?tool=Branding';
			pageMap['Jobs'] = '/openstorefront/admin?tool=Jobs';
			pageMap['Reports'] = '/openstorefront/admin?tool=Reports';
			pageMap['System'] = '/openstorefront/admin?tool=System';
			pageMap['Tracking'] = '/openstorefront/admin?tool=Tracking';
			pageMap['Messages'] = '/openstorefront/admin?tool=Messages';
			

			//Data Menu
			var dataMenu = [];
			dataMenu.push({
				text: 'Attributes',
				handler: function(){
					actionLoadContent('Attributes');
				}
			});
			dataMenu.push({
				text: 'Entries',
				handler: function(){
					actionLoadContent('Entries');
				}
			});
			dataMenu.push({
				text: 'Entry Type',
				handler: function(){
					actionLoadContent('EntryType');
				}
			});
			dataMenu.push({
				text: 'Highlights',
				handler: function(){
					actionLoadContent('Highlights');
				}
			});
			dataMenu.push({
				text: 'Integrations',
				handler: function(){
					actionLoadContent('Integrations');
				}
			});
			dataMenu.push({
				text: 'Imports',
				handler: function(){
					actionLoadContent('Imports');
				}
			});
			dataMenu.push({
				text: 'Lookups',
				handler: function(){
					actionLoadContent('Lookups');
				}
			});
			dataMenu.push({
				text: 'Media',
				handler: function(){
					actionLoadContent('Media');
				}
			});			
			dataMenu.push({
				text: 'Organizations',
				handler: function(){
					actionLoadContent('Organizations');
				}				
			});
			dataMenu.push({
				text: 'OLD Entries',
				handler: function(){
					actionLoadContent('EntriesOld');
				}
			});
			dataMenu.push({
				text: 'User Data',
				menu: {
					items: [
						{
							text: 'Questions',
							handler: function(){
								actionLoadContent('Questions');
							}							
						},
						{
							text: 'Reviews',
							handler: function(){
								actionLoadContent('Reviews');
							}							
						},
						{
							text: 'Tags',
							handler: function(){
								actionLoadContent('Tags');
							}							
						},
						{
							text: 'User Profiles',
							handler: function(){
								actionLoadContent('UserProfiles');
							}							
						}
					]
				}
			});
			
			var alertMenu = [];
			alertMenu.push({
				text: 'Alerts',
				handler: function(){
					actionLoadContent('Alerts');
				}
			});
			alertMenu.push({
				text: 'Branding',
				handler: function(){
					actionLoadContent('Branding');
				}				
			});
			alertMenu.push({
				text: 'Jobs',
				handler: function(){
					actionLoadContent('Jobs');
				}				
			});
			alertMenu.push({
				text: 'Reports',
				handler: function(){
					actionLoadContent('Reports');
				}				
			});
			alertMenu.push({
				text: 'System',
				handler: function(){
					actionLoadContent('System');
				}				
			});
			alertMenu.push({
				text: 'Tracking',
				handler: function(){
					actionLoadContent('Tracking');
				}				
			});			
			alertMenu.push({
				text: 'Messages',
				handler: function(){
					actionLoadContent('Messages');
				}				
			});
			alertMenu.push({
				xtype: 'menuseparator'				
			});
			alertMenu.push({
				text: 'API Documentation',
				href: '/openstorefront/API.action',
				hrefTarget: '_blank'
			});			

			var notificationWin = Ext.create('OSF.component.NotificationWindow', {				
			});	


			Ext.create('Ext.container.Viewport', {
				layout: 'border',
				items: [{
					region: 'north',					
					border: false,
					margin: '0 0 5 0',
					layout: 'hbox',
					items: [
						{
							html: document.getElementById('header').innerHTML,				
							flex: 1
						}, 
						{ 
							xtype: 'panel',
							cls: 'nav-back-color border_accent',
							padding: '10 8 10 10',
							layout: 'hbox',
							items: [
								{
									xtype: 'button',
									scale   : 'large',
									iconCls: 'fa fa-2x fa-envelope icon-top-padding',
									iconAlign: 'center',
									margin: '0 10 0 0',
									handler: function() {
										notificationWin.show();
										notificationWin.refreshData();
									}
								},
								{
									xtype: 'button',
									id: 'userMenuBtn',
									scale   : 'large',									
									text: 'User Menu',
									menu: {						
										items: [
											{
												text: 'Home',
												iconCls: 'fa fa-home',
												href: '../'
											},
											{
												xtype: 'menuseparator'
											},
											{
												text: '<b>Help</b>',
												iconCls: 'fa fa-question-circle',
												href: '../help',
												hrefTarget: '_blank'
											},
											{
												xtype: 'menuseparator'
											},
											{
												text: 'Logout',
												iconCls: 'fa fa-sign-out',
												href: '../Login.action?Logout'												
											}
										],
										listeners: {
											beforerender: function () {
											 this.setWidth(this.up('button').getWidth());
											}					
										}
									}
								}								
							]
						}
					]
				},
				{
					region: 'center',
					xtype: 'panel',
					layout: 'fit',					
					dockedItems: [
						{
							dock: 'top',
							xtype: 'toolbar',														
							items:[
								{
									text: 'Dashboard',
									scale   : 'large',
									iconCls: 'fa fa-2x fa-home',
									handler: function(){
										actionLoadContent('Dashboard');
									}									
								},
								{
									xtype: 'tbseparator'
								},
								{
									text: 'Data Management',
									scale   : 'large',
									iconCls: 'fa fa-2x fa-database',
									menu: {										
										items: dataMenu,
										listeners: {
											beforerender: function () {
											 this.setWidth(this.up('button').getWidth());
											}					
										}
									}
								},
								{
									text: 'Application Management',
									scale   : 'large',
									iconCls: 'fa fa-2x fa-gears',
									menu: {										
										items: alertMenu,
										listeners: {
											beforerender: function () {
											 this.setWidth(this.up('button').getWidth());
											}					
										}
									}
								}
							]
						}
					],					
					items: [
						contentPanel
					]
				}]
			});
			
			
			

			CoreService.usersevice.getCurrentUser().then(function(response, opts){
				var usercontext = Ext.decode(response.responseText);
				
				var userMenuText = usercontext.username;
				if (usercontext.firstName && usercontext.lastName)
				{
					userMenuText = usercontext.firstName + ' ' + usercontext.lastName;
				}
				Ext.getCmp('userMenuBtn').setText(userMenuText);				
				
				
				var socket = io.connect('', {
				  'resource':'openstorefront/event', 
				   query: 'id=' + usercontext.username
				});
				
				  socket.on('connect', function () {
					// console.warn(this.socket.transport.name + ' contected');
				  });
				  socket.on('WATCH', function (args) {
					
					var alert = {'type': args.entityMetaDataStatus ? alertStatus(args.entityMetaDataStatus): 'watch', 'msg': args.message + '<i>View the changes <a href="single?id='+args.entityId+'"><strong>here</strong></a>.</i>', 'id': 'watch_'+ args.eventId};
					handleAlert(alert, args);
				  });
				  socket.on('IMPORT', function (args) {					
					var alert = {'type': args.entityMetaDataStatus ? alertStatus(args.entityMetaDataStatus): 'import', 'msg': args.message, 'id': 'import_'+ args.eventId};					
					handleAlert(alert, args);
				  });
				  socket.on('TASK', function (args) {				
					var alert = {'type': args.entityMetaDataStatus ? alertStatus(args.entityMetaDataStatus): 'task', 'msg': args.message, 'id': 'task_'+ args.eventId};
					handleAlert(alert, args);
				  });
				  socket.on('REPORT', function (args) {					
					var alert = {'type': args.entityMetaDataStatus ? alertStatus(args.entityMetaDataStatus): 'report', 'msg': args.message + '<i>View/Download the report <a href="tools?tool=Reports"><strong>here</strong></a></i>.', 'id': 'report_'+ args.eventId};					
					handleAlert(alert, args);
				  });
				  socket.on('ADMIN', function (args) {					
					var alert = {'type': args.entityMetaDataStatus ? alertStatus(args.entityMetaDataStatus): 'admin', 'msg': '<i class="fa fa-warning"></i>&nbsp;' + args.message, 'id': 'admin_'+ args.eventId};
					handleAlert(alert, args);
				  });				
				
			});
			
			var alertStatus = function(status) {
				switch(status) {
				  case 'DONE':
				  return 'success';
				  break;
				  case 'CANCELLED':
				  return 'warning';
				  break;
				  case 'FAILED':
				  return 'danger';
				  break;
				  case 'QUEUED':
				  return 'warning';
				  break;
				  case 'WORKING':
				  return 'info';
				  break;
				  default:
				  return 'default';
				  break;
				}				
			};
			
			//Debounce....avoid dups (Coming from two instances; old admin page and the new)
			var lastNotificationEventId; 
			var handleAlert = function(alert, args) {
				var showMessage = true;
				if (lastNotificationEventId) {
					if (lastNotificationEventId === alert.id) {
						showMessage = false;
					}
				}
				if (showMessage) {					
					Ext.toast({
						html: alert.msg,
						title: 'Notification - ' + args.eventType,
						bodyCls: 'alert-' + alert.type,
						bodyPadding: 'padding: 40px;',
						closable: true,					
						minWidth: 200,
						align: 'br'
					});
					lastNotificationEventId = alert.id;
				}
			};
			
			var actionLoadContent = function(key) {
				var url = pageMap[key];
				if (url){					
					contents.load(url);				
					Ext.util.History.add(key);				
				} else {
					Ext.toast("Page key Not Found");
					contents.load('Router.action?page=admin/adminDashboard.jsp');	
				}
			};
			
			var historyToken = Ext.util.History.getToken();			
			if (historyToken) {
				actionLoadContent(historyToken);
			} else {	
				actionLoadContent('Dashboard');
			}	
			
			//Idle timeout check (Note: this probably as it doesn't cover all cases.)
			Ext.ns('CoreApp');

			CoreApp.BTN_OK = 'ok';
			CoreApp.BTN_YES = 'yes';

			// before notifying the user session will expire. Change this to a reasonable interval.    
			CoreApp.SESSION_ABOUT_TO_TIMEOUT_PROMT_INTERVAL_IN_MIN = 25;
			// 1 min. to kill the session after the user is notified.
			CoreApp.GRACE_PERIOD_BEFORE_EXPIRING_SESSION_IN_MIN = 1;
			// The page that kills the server-side session variables.
			CoreApp.SESSION_KILL_URL = '/openstorefront/Login.action?Logout';
			CoreApp.toMilliseconds = function (minutes) {
			  return minutes * 60 * 1000;
			};

			CoreApp.simulateAjaxRequest = function () {
				Ext.Ajax.request({
					url: '../api/v1/resource/userprofiles/currentuser'
				});
			};

//FIXME: Handle this better
			CoreApp.sessionAboutToTimeoutPromptTask = new Ext.util.DelayedTask(function () {
				CoreApp.simulateAjaxRequest();
/**				
				Ext.Msg.confirm(
					'Your Session is About to Expire',
					Ext.String.format('Your session will expire in {0} minute(s). Would you like to continue your session?',
						CoreApp.GRACE_PERIOD_BEFORE_EXPIRING_SESSION_IN_MIN),
					function (btn, text) {
						if (btn == CoreApp.BTN_YES) {
							// Simulate resetting the server-side session timeout timer
							// by sending an AJAX request.
							CoreApp.simulateAjaxRequest();
						} else {
							// Send request to kill server-side session.
							window.location.replace(CoreApp.SESSION_KILL_URL);
						}
					}
				);
**/				
				CoreApp.killSessionTask.delay(CoreApp.toMilliseconds(
				CoreApp.GRACE_PERIOD_BEFORE_EXPIRING_SESSION_IN_MIN));
			});
			CoreApp.killSessionTask = new Ext.util.DelayedTask(function () {        
				window.location.replace(CoreApp.SESSION_KILL_URL);
			});			
			
			Ext.Ajax.on('requestcomplete', function(conn, response, options){
			   if (options.url !== CoreApp.SESSION_KILL_URL) {
					// Reset the client-side session timeout timers.
					// Note that you must not reset if the request was to kill the server-side session.
					CoreApp.sessionAboutToTimeoutPromptTask.delay(CoreApp.toMilliseconds(CoreApp.SESSION_ABOUT_TO_TIMEOUT_PROMT_INTERVAL_IN_MIN));
					CoreApp.killSessionTask.cancel();
				} else {
					// Notify user her session timed out.
					Ext.Msg.alert(
						'Session Expired',
						'Your session expired. Please login to start a new session.',
						function (btn, text) {
							if (btn == CoreApp.BTN_OK) {
								window.location.replace('/openstorefront/Login.action');
							}
						}
					);
				}
			});

		});
			
	</script>
    </stripes:layout-component>
</stripes:layout-render>
        
