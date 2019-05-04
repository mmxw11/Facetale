package wepa.ftale.web;

import java.util.Locale;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * @author Matias
 */
@Component
public class FormUtils {

    @Autowired
    private MessageSource messageSource;
    private static MessageSourceAccessor accessor;

    @PostConstruct
    private void initClass() {
        accessor = new MessageSourceAccessor(messageSource, Locale.ENGLISH);
    }

    /**
     * Check if form has any validation errors and preserve them during the next redirect.
     * @param formObject
     * @param data
     * @param bindingResult
     * @param rdAttributes
     * @return whether there were any errors
     */
    public static boolean redirectBindingResult(String formObject, Object data, BindingResult bindingResult, RedirectAttributes rdAttributes) {
        if (!bindingResult.hasErrors()) {
            return false;
        }
        rdAttributes.addFlashAttribute("org.springframework.validation.BindingResult." + formObject, bindingResult);
        rdAttributes.addFlashAttribute(formObject, data);
        return true;
    }

    public static String formatBindingResultErrors(BindingResult bindingResult) {
        // Tämä on vähän "patenttiratkaisu", mutta toiminee tähän tilanteeseen.
        StringBuilder sbuilder = new StringBuilder();
        for (ObjectError error : bindingResult.getAllErrors()) {
            if (error instanceof FieldError) {
                FieldError fe = (FieldError) error;
                sbuilder.append(fe.getField());
                sbuilder.append(": ");
            }
            sbuilder.append(accessor.getMessage(error));
        }
        return sbuilder.toString();
    }
}