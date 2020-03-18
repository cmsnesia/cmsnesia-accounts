package com.cmsnesia.accounts.domain.validator.impl;

import com.cmsnesia.accounts.domain.validator.Password;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class PasswordValidator implements ConstraintValidator<Password, CharSequence> {

  @Override
  public boolean isValid(CharSequence value, ConstraintValidatorContext context) {
    if (value.length() < 6) {
      return false;
    }
    return true;
  }
}
