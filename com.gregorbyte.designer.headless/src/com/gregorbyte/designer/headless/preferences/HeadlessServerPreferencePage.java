package com.gregorbyte.designer.headless.preferences;

import org.eclipse.jface.preference.*;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.IWorkbench;

import com.gregorbyte.designer.headless.HeadlessServerActivator;

/**
 * This class represents a preference page that is contributed to the
 * Preferences dialog. By subclassing <samp>FieldEditorPreferencePage</samp>, we
 * can use the field support built into JFace that allows us to create a page
 * that is small and knows how to save, restore and apply itself.
 * <p>
 * This page is used to modify preferences only. They are stored in the
 * preference store that belongs to the main plug-in class. That way,
 * preferences can be accessed directly via the preference store.
 */

public class HeadlessServerPreferencePage extends FieldEditorPreferencePage
		implements IWorkbenchPreferencePage {

	public HeadlessServerPreferencePage() {
		super(GRID);
		setPreferenceStore(HeadlessServerActivator.getDefault()
				.getPreferenceStore());
		setDescription("These are preferences to do with Gregorbyte Headless Designer Server");
	}

	/**
	 * Creates the field editors. Field editors are abstractions of the common
	 * GUI blocks needed to manipulate various types of preferences. Each field
	 * editor knows how to save and restore itself.
	 */
	public void createFieldEditors() {
		addField(new StringFieldEditor(PreferenceConstants.P_PORT,
				"Port Number to Listen on:", getFieldEditorParent()));

		addField(new BooleanFieldEditor(PreferenceConstants.P_AUTOSTART,
				"Start Headless Server Automatically When Designer Starts", getFieldEditorParent()));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
	 */
	public void init(IWorkbench workbench) {
	}

}