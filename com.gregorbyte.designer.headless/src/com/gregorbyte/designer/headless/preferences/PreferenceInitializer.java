package com.gregorbyte.designer.headless.preferences;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;

import com.gregorbyte.designer.headless.HeadlessServerActivator;

/**
 * Class used to initialize default preference values.
 */
public class PreferenceInitializer extends AbstractPreferenceInitializer {

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer#initializeDefaultPreferences()
	 */
	public void initializeDefaultPreferences() {
		IPreferenceStore store = HeadlessServerActivator.getDefault().getPreferenceStore();	
		store.setDefault(PreferenceConstants.P_PORT, "8282");
		store.setDefault(PreferenceConstants.P_AUTOSTART, true);
	}

}
