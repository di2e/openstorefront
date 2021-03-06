/* 
 * Copyright 2018 Space Dynamics Laboratory - Utah State University Research Foundation.
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

/* Author: Brigham Michaelis */

/* global Ext */

Ext.define('OSF.customSubmission.field.RelationshipsGrid', {
	extend: 'OSF.customSubmission.SubmissionBaseGrid',
	xtype: 'osf-submissionform-relationshipgrid',
	requires: [
		'OSF.customSubmission.form.Relationships'
	],
	
	title: '',
	fieldType: 'RELATIONSHIPS_MULTI',
	
	columns: [
		{ text: 'Relation Type', dataIndex: 'relationshipTypeDescription', width: 250 },
		{ text: 'Entry Name', dataIndex: 'targetComponentName', flex: 1, minWidth: 200 }
	],
	
	initComponent: function () {
		var grid = this;
		grid.callParent();	
		
		if (grid.section) {
			var initialData = grid.section.submissionForm.getFieldData(grid.fieldTemplate.fieldId);
			if (initialData) {
				var data = Ext.decode(initialData);	
				
				//map missing fields
				Ext.Array.each(data, function(dataItem){
					dataItem.relatedComponentId = dataItem.targetComponentId;
					dataItem.componentRelationshipId = dataItem.relationshipId;
				});				
				
				grid.getStore().loadData(data);
			}			
		}
		
		grid.queryById('editBtn').setHidden(true);
		
	},	
	
	actionAddEdit: function(record) {
		var grid = this;
		
		var addEditWin = Ext.create('Ext.window.Window', {
			title: 'Add/Edit Relationship',
			modal: true,
			width: 800,
			height: 310,
			alwaysOnTop: 99,
			closeMode: 'destroy',
			layout: 'fit',
			items: [
				{
					xtype: 'osf-submissionform-relationships',
					itemId: 'form',
					scrollable: true,
					originalRecord: record,
					dockedItems: [
						{
							xtype: 'toolbar',
							dock: 'bottom',
							items: [
								{
									text: 'Save',
									formBind: true,
									iconCls: 'fa fa-lg fa-edit icon-button-color-edit',
									handler: function () {
										var form = this.up('form');
										var data = form.getValues();
										
										data.relationshipTypeDescription = form.queryById('relationshipType').getSelection().get('description');										
										data.targetComponentName = form.queryById('relationshipTargetCB').getSelection().get('description');
										
										if (record) {
											record.set(data, {
												dirty: false
											});
										} else {
											grid.getStore().add(data);
										}
										this.up('window').close();
									}
								},
								{
									xtype: 'tbfill'
								},
								{
									text: 'Cancel',
									iconCls: 'fa fa-lg fa-close icon-button-color-warning',
									handler: function () {
										this.up('window').close();												
									}
								}								
							]
						}
					]
				}
			]
			
		});
		addEditWin.show();
		
		if (record) {
			addEditWin.queryById('form').loadRecord(record);
		}		
		
	},
	
	showOnEntryType: function() {
		var grid = this;		
		return grid.componentType.dataEntryRelationships || false;		
	},
	getUserData: function() {
		var grid = this;
		
		var data = [];
		grid.getStore().each(function(record){
			data.push(record.getData());
		});
		
		var userSubmissionField = {			
			templateFieldId: grid.fieldTemplate.fieldId,
			rawValue: Ext.encode(data)
		};		
		return userSubmissionField;			
	}	
	
});
