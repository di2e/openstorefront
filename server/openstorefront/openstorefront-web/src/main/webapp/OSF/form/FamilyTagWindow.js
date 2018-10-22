/* 
 * Copyright 2017 Space Dynamics Laboratory - Utah State University Research Foundation.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * See NOTICE.txt for more information.
 */
/* global Ext, CoreUtil */
Ext.define('OSF.form.FamilyTagWindow', {
    extend: 'Ext.window.Window',
    alias: 'osf.form.FamilyTagWindow',

    title: 'Are you sure you want to add a new tag? ',
    width: '70%',
    height: 450,
    y: 200,
    modal: true,
    layout: 'fit',
    closeAction: 'destroy',
    processTagsCallback: null,

    initComponent: function(){
        this.callParent();
        var familyTagWindow = this;
        familyTagWindow.winText = '<h3> Are you sure that you would like to add a new tag? </br> ' + 
        ' Please see other possible matches below.</h3>';

        var saveTagToComponent = function(tag){
            // If there are related tags then show the window.
            if (!tag || tag === '') {
                Ext.getCmp('tagField').markInvalid('Tag name required');
            } else {	
                // Otherwise just add the tag.
                Ext.getCmp('tagPanel').setLoading('Tagging Entry...');
                Ext.Ajax.request({
                    url: 'api/v1/resource/components/' + familyTagWindow.tagPanel.componentId + '/tags',
                    method: 'POST',
                    jsonData: {
                        text: tag
                    },
                    callback: function(){
                        Ext.getCmp('tagPanel').setLoading(false);
                    },
                    success: function(response, opt){
                        var tag = Ext.decode(response.responseText);
                        var tagField = Ext.getCmp('tagField');
    
                        if (typeof tag.errors === 'undefined') {
                            if(familyTagWindow.processTagsCallback){
                                familyTagWindow.processTagsCallback(tag);
                            }
                            tagField.reset();
                            tagField.getStore().load();
                        }
                        else {
                            tagField.reset();
                            tagField.markInvalid(tag.errors.entry[0].value);
                        }
                    }
                });	
            }
        }
        


        familyTagWindow.tagPanel = Ext.create('Ext.panel.Panel', {
            preventDefaultAction: true,
            saveCallBack: null, // add a callback
            layout: 'fit'
        });
        familyTagWindow.add(familyTagWindow.tagPanel);
        

        familyTagWindow.tagPanel.tagGrid = Ext.create('Ext.grid.Panel', {
            layout: 'fit',

            viewConfig: {
                enableTextSelection: true
            },
            store: Ext.create('Ext.data.Store', {
                fields: [			
                    {
                        name: 'createDts',
                        type:	'date',
                        dateFormat: 'c'
                    }														
                ],
                autoLoad: false,
                proxy: {
                    type: 'ajax'							
                }
            }),
            columns: [
                { text: 'Related Tags', dataIndex: 'text', flex: 1, minWidth: 100 }
            ],
            listeners: {
                selectionchange: function(grid, record, index, opts){
                    var fullgrid = familyTagWindow.tagPanel.tagGrid;
                    if (fullgrid.getSelectionModel().getCount() === 1) {
                        fullgrid.down('toolbar').getComponent('useOldTag').setDisabled(false);
                    } else {
                        fullgrid.down('toolbar').getComponent('useOldTag').setDisabled(true);
                    }
                }						
            },
            dockedItems: [
                {
                    xtype: 'panel',
                    dock: 'top',
                    html: familyTagWindow.winText
                },
                {
                    xtype: 'toolbar',
                    dock: 'bottom',
                    items: [
                        {
                            text: 'Yes, I am sure, add the new tag.',
                            iconCls: 'fa fa-lg fa-save icon-button-color-save',
                            handler: function(){
                                saveTagToComponent(familyTagWindow.tagPanel.tagGrid.newTag);
                                this.up('window').close();
                            }
                        },
                        {
                            xtype: 'tbfill'
                        },
                        {
                            text: 'No, I want to use the selected prexisting tag.',
                            itemId: 'useOldTag',
                            iconCls: 'fa fa-lg fa-save icon-button-color-save',
                            disabled: true,
                            handler: function(){
                                saveTagToComponent(familyTagWindow.tagPanel.tagGrid.getSelection()[0].data.text);
                                this.up('window').close();
                            }
                        },
                        {
                            xtype: 'tbfill'
                        },
                        {
                            text: 'Cancel',
                            iconCls: 'fa fa-lg fa-close icon-button-color-warning',
                            handler: function(){
                                this.up('window').close();
                            }
                        }
                    ]
                }
            ]
        })	


        familyTagWindow.tagPanel.add(familyTagWindow.tagPanel.tagGrid);
    },
	loadData: function(componentId, newTag) {
		
        var familyTagWindow = this;
		
		familyTagWindow.tagPanel.componentId = componentId;
		familyTagWindow.tagPanel.tagGrid.newTag = newTag;
		familyTagWindow.tagPanel.tagGrid.componentId = componentId;
		
		familyTagWindow.tagPanel.tagGrid.getStore().load({
			url: 'api/v1/resource/components/' + componentId + '/relatedtags'
		});		

	}


});