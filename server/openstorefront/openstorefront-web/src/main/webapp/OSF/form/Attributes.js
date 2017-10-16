/* 
 * Copyright 2016 Space Dynamics Laboratory - Utah State University Research Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
/* global Ext, CoreUtil */
Ext.define('OSF.form.Attributes', {
	extend: 'Ext.panel.Panel',
	alias: 'osf.form.Attributes',

	layout: 'fit',
	initComponent: function () {

		this.callParent();

		var attributePanel = this;

		attributePanel.loadComponentAttributes = function (status) {
			if (!status) {
				var tools = attributePanel.attributeGrid.getComponent('tools');
				status = tools.getComponent('attributeFilterActiveStatus').getValue();
			}

			attributePanel.attributeGrid.setLoading(true);
			var componentId = attributePanel.componentId;
			Ext.Ajax.request({
				url: 'api/v1/resource/components/' + componentId + '/attributes/view',
				method: 'GET',
				params: {
					status: status
				},
				callback: function () {
					attributePanel.attributeGrid.setLoading(false);
				},
				success: function (response, opts) {
					var data = Ext.decode(response.responseText);

					var optionalAttributes = [];
					Ext.Array.each(data, function (attribute) {
						if (!attribute.requiredFlg) {
							optionalAttributes.push(attribute);
						}
					});
					optionalAttributes.reverse();
					attributePanel.attributeGrid.getStore().loadData(optionalAttributes);
				}
			});
		};

		attributePanel.attributeGrid = Ext.create('Ext.grid.Panel', {
			columnLines: true,
			store: Ext.create('Ext.data.Store', {
				fields: [
					"type",
					"code",
					"typeDescription",
					"codeDescription",
					"orphan",
					"activeStatus",
					{
						name: 'updateDts',
						type: 'date',
						dateFormat: 'c'
					}
				],
				autoLoad: false,
				proxy: {
					type: 'ajax'
				}
			}),
			columns: [
				{text: 'Attribute Type', dataIndex: 'typeDescription', width: 200},
				{text: 'Attribute Code', dataIndex: 'codeDescription', flex: 1, minWidth: 200},
				{text: 'Update Date', dataIndex: 'updateDts', width: 175, xtype: 'datecolumn', format: 'm/d/y H:i:s'}
			],
			listeners: {
				selectionchange: function (grid, record, index, opts) {
					var fullgrid = attributePanel.attributeGrid;
					if (fullgrid.getSelectionModel().getCount() === 1) {
						fullgrid.down('toolbar').getComponent('toggleStatusBtn').setDisabled(false);
						fullgrid.down('toolbar').getComponent('removeBtn').setDisabled(false);
					} else {
						fullgrid.down('toolbar').getComponent('toggleStatusBtn').setDisabled(true);
						fullgrid.down('toolbar').getComponent('removeBtn').setDisabled(true);
					}
				}
			},
			dockedItems: [
				{
					xtype: 'form',
					title: 'Add Attribute',
					collapsible: true,
					titleCollapse: true,
					border: true,
					layout: 'vbox',
					bodyStyle: 'padding: 10px;',
					margin: '0 0 5 0',
					defaults: {
						labelAlign: 'top',
						labelSeparator: '',
						width: '100%'
					},
					buttonAlign: 'center',
					buttons: [
						{
							xtype: 'button',
							text: 'Save',
							formBind: true,
							margin: '0 20 0 0',
							iconCls: 'fa fa-lg fa-save',
							handler: function () {
								var form = this.up('form');
								var data = form.getValues();
								var componentId = attributePanel.componentId;

								data.componentAttributePk = {
									attributeType: data.attributeType,
									attributeCode: data.attributeCode
								};
								var valid = true;
								var selectedAttributes = form.queryById('attributeTypeCB').getSelection();
								var attributeType = selectedAttributes.data;
								if (attributeType.attributeValueType === 'NUMBER' && Ext.String.endsWith(data.attributeCode, ".")) {																			//check percision; this will enforce max allowed
									try {
										var valueNumber = new Number(data.attributeCode);
										if (isNaN(valueNumber))
											throw "Bad Format";
										data.attributeCode = valueNumber.toString();
										data.componentAttributePk.attributeCode = valueNumber.toString();
									} catch (e) {
										valid = false;
										form.getForm().markInvalid({
											attributeCode: 'Number must not have a decimal point or have at least one digit after the decimal point.'
										});
									}
								}
								if (valid)
								{
									CoreUtil.submitForm({
										url: 'api/v1/resource/components/' + componentId + '/attributes',
										method: 'POST',
										data: data,
										form: form,
										success: function () {
											attributePanel.loadComponentAttributes();
											form.reset();
										}
									});
								}
							}
						},
						{
							xtype: 'button',
							text: 'Cancel',
							iconCls: 'fa fa-lg fa-close',
							handler: function () {
								this.up('form').reset();
							}
						},
						{
							xtype: 'button',
							text: 'Add Multiple Attributes',
							iconCls: 'fa fa-lg fa-plus',
							handler: function () {
								var getAttributeFormPanelItems = function ()
								{
									formPanel.setLoading(true);
									Ext.Ajax.request({
										url: 'api/v1/resource/attributes/optional?componentType=' + attributePanel.component.componentType,
										callback: function () {
											formPanel.setLoading(false);
										},
										success: function (response, opts) {
											var items = new Array();
											var attributes = Ext.decode(response.responseText);
											var valueTypes = [];
											Ext.Array.forEach(attributes, function (attribute, key) {
												var label = attribute.description;
												if (attribute.detailedDescription !== undefined)
												{
													label = Ext.String.format('{0} <i class="fa fa-question-circle"  data-qtip="{1}"></i>', attribute.description, attribute.detailedDescription.replace(/"/g, '&quot;'));
												}
												var vtype = undefined;
												if (attribute.attributeValueType === 'NUMBER')
												{
													vtype = 'AttributeNumber';
													valueTypes[attribute.attributeType] = attribute.attributeValueType;
												}
												var item = {
													name: attribute.attributeType,
													itemId: 'multiAttributeCode_' + attribute.attributeType,
													width: '98%',
													labelStyle: 'width:300px',
													labelWidth: '100%',
													xtype: 'combobox',
													margin: '10 0 10 10',
													fieldLabel: label,
													queryMode: 'local',
													editable: attribute.allowUserGeneratedCodes,
													typeAhead: false,
													allowBlank: true,
													valueField: 'code',
													displayField: 'label',
													vtype: vtype,
													store: Ext.create('Ext.data.Store', {
														fields: [
															"code",
															"label"
														],
														data: attribute.codes
													})
												};
												items.push(item);
											});
											formPanel.add(items);
											formPanel.valueTypes = valueTypes;
										}
									});
								};
								var formPanel = Ext.create('Ext.form.Panel', {
									layout: 'anchor',
									scrollable: true
								});
								var multipleAttributesWin = Ext.create('Ext.window.Window', {
									title: 'Add Attributes',
									iconCls: 'fa fa-lg fa-plus icon-small-vertical-correction',
									modal: true,
									width: 700,
									closeAction: 'destroy',
									height: '50%',
									layout: 'fit',
									items: [
										formPanel
									],
									dockedItems: [{
											xtype: 'toolbar',
											itemId: 'buttonToolBar',
											dock: 'bottom',
											items: [
												{
													xtype: 'tbfill'
												},
												{
													xtype: 'button',
													text: 'Save',
													formBind: true,
													margin: '0 20 0 0',
													iconCls: 'fa fa-lg fa-save',
													handler: function () {
														var postData = [];
														var valid = true;
														if (formPanel.getForm().isValid())
														{
															var rawData = formPanel.getValues();
															var componentId = attributePanel.componentId;
															Ext.Object.each(rawData, function (key, value) {
																if (value)
																{
																	if (this.valueTypes[key] === 'NUMBER' && Ext.String.endsWith(value, ".")) {																			//check percision; this will enforce max allowed
																		try {
																			var valueNumber = new Number(value);
																			if (isNaN(valueNumber))
																				throw "Bad Format";
																			value = valueNumber.toString();
																		} catch (e) {
																			valid = false;
																			var dataError = {};
																			dataError[key] = 'Number must not have a decimal point or have at least one digit after the decimal point.';
																			formPanel.getForm().markInvalid(dataError);
																		}
																	}
																	if (valid)
																	{
																		postData.push({
																			attributeType: key,
																			attributeCode: value,
																			componentAttributePk: {
																				attributeType: key,
																				attributeCode: value
																			}
																		});
																	}
																}
															}, formPanel);
														}

														if (valid) {
															CoreUtil.submitForm({
																url: 'api/v1/resource/components/' + componentId + '/attributeList',
																method: 'POST',
																data: postData,
																form: formPanel,
																success: function () {
																	attributePanel.loadComponentAttributes();
																	formPanel.reset();
																	formPanel.up('window').close();
																}
															});
														} else {
															Ext.Msg.show({
																title: 'Form Validation Error',
																message: 'There are errors in the attributes submitted',
																buttons: Ext.Msg.OK,
																icon: Ext.Msg.ERROR
															});
														}
													}
												},
												{
													xtype: 'button',
													text: 'Cancel',
													iconCls: 'fa fa-lg fa-close',
													handler: function () {
														var rawData = formPanel.getValues();
														var unSavedData = false;
														Ext.Object.each(rawData, function (key, value) {
															if (value)
															{
																unSavedData = true;
																return false;
															}
														});
														if (unSavedData)
														{
															Ext.Msg.show({
																title: 'Unsaved data',
																message: 'Warning unsaved unsaved data will be lost.',
																buttons: Ext.Msg.YESNO,
																buttonText: {
																	yes: "OK",
																	no: "Cancel"
																},
																icon: Ext.Msg.WARNING,
																fn: function (btn) {
																	if (btn === 'yes') {
																		formPanel.up('window').close();
																	}
																}
															});

														} else
														{
															formPanel.up('window').close();
														}
													}
												},
												{
													xtype: 'tbfill'
												}
											]
										}
									]
								});
								getAttributeFormPanelItems();
								multipleAttributesWin.show();
							}
						}
					],
					items: [
						{
							xtype: 'combobox',
							itemId: 'attributeTypeCB',
							fieldLabel: 'Attribute Type <span class="field-required" />',
							name: 'attributeType',
							forceSelection: true,
							queryMode: 'local',
							editable: true,
							typeAhead: true,
							allowBlank: false,
							valueField: 'attributeType',
							displayField: 'description',
							store: {
								autoLoad: false,
								proxy: {
									type: 'ajax',
									url: 'api/v1/resource/attributes'
								},
								filters: [
									{
										property: 'requiredFlg',
										value: 'false'
									}
								],
								listeners: {
									load: function (store, records, opts) {
										store.filterBy(function (attribute) {
											if (attribute.data.associatedComponentTypes) {
												var optFound = Ext.Array.findBy(attribute.data.associatedComponentTypes, function (item) {
													if (item.componentType === attributePanel.component.componentType) {
														return true;
													} else {
														return false;
													}
												});
												if (optFound) {
													return true;
												} else {
													return false;
												}
											} else {
												return true;
											}
										});
									}
								}
							},
							listeners: {
								change: function (field, newValue, oldValue, opts) {
									var cbox = field.up('form').getComponent('attributeCodeCB');
									cbox.clearValue();

									var record = field.getSelection();
									if (record) {
										cbox.getStore().loadData(record.data.codes);
										cbox.vtype = (record.data.attributeValueType === 'NUMBER') ? 'AttributeNumber' : undefined;
										cbox.setEditable(record.get("allowUserGeneratedCodes"));
									} else {
										cbox.getStore().removeAll();
										cbox.vtype = undefined;
									}
								}
							}
						},
						{
							xtype: 'combobox',
							itemId: 'attributeCodeCB',
							fieldLabel: 'Attribute Code <span class="field-required" />',
							name: 'attributeCode',
							queryMode: 'local',
							editable: false,
							typeAhead: false,
							allowBlank: false,
							valueField: 'code',
							displayField: 'label',
							store: Ext.create('Ext.data.Store', {
								fields: [
									"code",
									"label"
								]
							})
						}
					]
				},
				{
					xtype: 'toolbar',
					itemId: 'tools',
					items: [
						{
							xtype: 'combobox',
							itemId: 'attributeFilterActiveStatus',
							fieldLabel: 'Filter Status',
							store: {
								data: [
									{code: 'A', description: 'Active'},
									{code: 'I', description: 'Inactive'}
								]
							},
							forceSelection: true,
							queryMode: 'local',
							displayField: 'description',
							valueField: 'code',
							value: 'A',
							listeners: {
								change: function (combo, newValue, oldValue, opts) {
									attributePanel.loadComponentAttributes(newValue);
								}
							}
						},
						{
							text: 'Refresh',
							iconCls: 'fa fa-lg fa-refresh icon-button-color-refresh',
							handler: function () {
								attributePanel.loadComponentAttributes();
							}
						},
						{
							xtype: 'tbseparator'
						},
						{
							text: 'Toggle Status',
							itemId: 'toggleStatusBtn',
							iconCls: 'fa fa-lg fa-power-off icon-button-color-default',
							disabled: true,
							handler: function () {
								CoreUtil.actionSubComponentToggleStatus(attributePanel.attributeGrid, 'type', 'attributes', 'code', null, null, function () {
									attributePanel.loadComponentAttributes();
								});
							}
						},
						{
							xtype: 'tbfill'
						},
						{
							text: 'Delete',
							itemId: 'removeBtn',
							iconCls: 'fa fa-trash fa-lg icon-button-color-warning',
							disabled: true,
							handler: function () {
								CoreUtil.actionSubComponentToggleStatus(attributePanel.attributeGrid, 'type', 'attributes', 'code', null, true, function () {
									attributePanel.loadComponentAttributes();
								});
							}
						}
					]
				}
			]
		});

		attributePanel.add(attributePanel.attributeGrid);
	},
	loadData: function (evaluationId, componentId, data, opts) {
		//just load option (filter out required)
		var attributePanel = this;

		attributePanel.componentId = componentId;
		attributePanel.attributeGrid.componentId = componentId;
		attributePanel.loadComponentAttributes();

		var form = attributePanel.attributeGrid.down('form');
		form.setLoading(true);
		Ext.Ajax.request({
			url: 'api/v1/resource/components/' + attributePanel.componentId,
			callback: function () {
				form.setLoading(false);
			},
			success: function (response, opts) {
				var component = Ext.decode(response.responseText);
				attributePanel.component = component;
				attributePanel.attributeGrid.down('form').getComponent('attributeTypeCB').getStore().load();
			}
		});


		if (opts && opts.commentPanel) {
			opts.commentPanel.loadComments(evaluationId, "Attribute", componentId);
		}
	}

});

// custom Vtype (validator) for vtype:'AttributeNumber'
Ext.define('Override.form.field.VTypes', {
	override: 'Ext.form.field.VTypes',

	AttributeNumber: function (value) {
		return this.AttributeNumberRe.test(value);
	},
	// Any number of digits on whole nuumbers and 0-20 digits for decimal precision
	AttributeNumberRe: /^\d*(\.\d{0,20})?$/,
	AttributeNumberText: 'Must be numeric with decimal precision less than or equal to 20.'
			// Mask forces only charaters meeting the regular expersion are
			// allowed to be entered. We decided to not to enforce a mask so 
			// useres can tell the difference between readOnly fields and 
			// incorrect input

			// AttributeNumberMask: /[\d\.]/i
});
