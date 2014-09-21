package com.own.exercise.validators;

import com.vaadin.data.Validator.InvalidValueException;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.TextField;

public class InputValidators {

	public boolean validateField(int index, FormLayout layout){
		if (layout.getComponent(index).getCaption().contains("name")) {
			try {
			((TextField)(layout.getComponent(index))).setValidationVisible(true);
			((TextField)(layout.getComponent(index))).validate();
			} catch (InvalidValueException e) {
				Notification.show(e.getMessage());
				return false;
			}
			return true;
		} else if (layout.getComponent(index).getCaption().contains("Gender")) {
			try {
			((OptionGroup)(layout.getComponent(index))).
				setValidationVisible(true);
			((OptionGroup)(layout.getComponent(index))).validate();
			} catch (InvalidValueException e) {
				Notification.show(e.getMessage());
				return false;
			}
			return true;
		} else { 
			/* Possible other fields does not need validation */
			return true;
		}		
	}
}
