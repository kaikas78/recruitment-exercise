package com.own.exercise.formview;

import javax.servlet.annotation.WebServlet;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.data.util.ObjectProperty;
import com.vaadin.data.validator.NullValidator;
import com.vaadin.data.validator.RegexpValidator;
import com.vaadin.event.FieldEvents.TextChangeEvent;
import com.vaadin.event.FieldEvents.TextChangeListener;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.own.exercise.entities.EntityManagerFunction;
import com.own.exercise.validators.InputValidators;

@SuppressWarnings("serial")
@Theme("formview")
public class FormviewUI extends UI {

	@WebServlet(value = "/*", asyncSupported = true)
	@VaadinServletConfiguration(productionMode = false, ui = FormviewUI.class)
	public static class Servlet extends VaadinServlet {
	}
    /* Master layout for form fields */
	private final FormLayout layout = createForm();
	
	@Override
	protected void init(VaadinRequest request) {
		layout.setStyleName("mytheme");		
		layout.addComponent(createButton("Commit to DB"));
		
	}
	
	/* Function for creating needed form layout and target fields */
	private FormLayout createForm(){		
		final FormLayout fLayout = new FormLayout();
		final Label count = new Label();
		
		setContent(fLayout);
		fLayout.addComponent(createTextField("First name"));
		fLayout.addComponent(createTextField("Last name"));
		
		OptionGroup gender = new OptionGroup("Gender");
		gender.addItem("Male"); 
		gender.addItem("Female");
		gender.setNullSelectionAllowed(false);
		gender.setRequired(true);
		gender.setRequiredError("Gender is needed to specify!"); 
		gender.addValidator(new NullValidator(
							"Gender is needed to specify", false));
		gender.setValidationVisible(false);
		
		final TextArea reason = new TextArea("Why are you applying "
				+ "to this job?");
		/* No validation needed for this field. Length can be 0-1000 characters
		 * max value is checked by TextArea property maxLength
		 */
		reason.setMaxLength(1000);
		reason.addTextChangeListener(new TextChangeListener() {
			
			@Override
			public void textChange(TextChangeEvent event) {
				// TODO Auto-generated method stub
				count.setValue(event.getText().length() + " of " + 
						reason.getMaxLength());;
			}
		});
		fLayout.addComponents(gender, reason, count);        		
		return fLayout;
	}

	/* Function for creating text fields and setting needed validation rules */
	private TextField createTextField(String name) {
		final ObjectProperty<String> property = new ObjectProperty<String>("");
		property.setValue(null);
		TextField fieldName = new TextField(name, property);
		fieldName.setBuffered(true);
		fieldName.setRequired(true);
		fieldName.setRequiredError(name + " cannot be empty!"); 
		fieldName.setNullRepresentation("");
		fieldName.setImmediate(true);
		fieldName.setNullSettingAllowed(true);
		fieldName.setMaxLength(50);
		fieldName.addValidator(new RegexpValidator("[a-öA-Ö-]+",name + 
							" contains illegal characters, aA-öÖ allowed"));
		fieldName.addValidator(new RegexpValidator("^.{3," +fieldName.
				getMaxLength()+"}$",name + " length can be 3-50 characters"));
		fieldName.setValidationVisible(false);
		
		return fieldName;
		
	}
	
	/* Function for creating needed buttons */
	private Button createButton(String name) {
		Button button = new Button(name);
		button.addClickListener(new Button.ClickListener() {
			public void buttonClick(ClickEvent event) {
				//ResponderBean responder = new ResponderBean();
				InputValidators isValid = new InputValidators();
				boolean result = false;
				/* Validate needed fields
				 * count-3 because count starts zero and two last component are
		         * label and button, which is not needed to handle here 
				 */
				for (Integer i = 0; i <= layout.getComponentCount()-3; i++) {										
					result = isValid.validateField(i, layout);
					if (result == false) {
						/* Validation failure, no need to continue */
						break;
					}
				}
				if (result != false ) {
					/* Validations succeeded, store data to DB and go to
					 * show data view.
					 */
					EntityManagerFunction emf = new EntityManagerFunction();
					Boolean storingResult = emf.storeData(layout);
					if (storingResult == true ) {
						showCommittedData();
					} else {
						Notification.show("Something went wrong during storing"
								+ "data. Please verify inputted data and try "
								+ "commit again");
					}
				}
			}
		});
		return button;
	}
	
	/* Function for hiding form fields and showing end comments and 
	 * stored data values from DB.
	 */
	private void showCommittedData () {
		Label label = new Label("Data have been stored to DB. You can now "
				+ "close window. Thank you.");
		
		layout.getComponent(layout.getComponentCount()-1).setVisible(false);
		layout.getComponent(layout.getComponentCount()-2).setVisible(false);
		for (Integer i = 0; i <= layout.getComponentCount()-1; i++) {										
			(layout.getComponent(i)).setVisible(false);    					
		}
		layout.addComponent(label);
		EntityManagerFunction ent = new EntityManagerFunction();
		ent.getData(layout);
		
	}

}