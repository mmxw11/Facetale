package wepa.ftale.web;

import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * @author Matias
 */
public class FormUtils {

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
}