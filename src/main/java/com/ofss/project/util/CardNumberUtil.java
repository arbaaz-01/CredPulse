package com.ofss.project.util;

import org.springframework.stereotype.Component;

@Component
public class CardNumberUtil {

	public boolean isValid(String cardNumber) {

		if (cardNumber == null || !cardNumber.matches("\\d{13,19}")) {
			return false;
		}

		int sum = 0;
		boolean doubleDigit = false;

		for (int i = cardNumber.length() - 1; i >= 0; i--) {

			int digit = cardNumber.charAt(i) - '0';

			if (doubleDigit) {

				digit *= 2;

				if (digit > 9) {
					digit -= 9;
				}
			}

			sum += digit;

			doubleDigit = !doubleDigit;
		}

		return sum % 10 == 0;
	}
}
