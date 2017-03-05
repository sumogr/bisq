/*
 * This file is part of Bitsquare.
 *
 * Bitsquare is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or (at
 * your option) any later version.
 *
 * Bitsquare is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public
 * License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with Bitsquare. If not, see <http://www.gnu.org/licenses/>.
 */

package io.bitsquare.gui.util.validation;

import io.bitsquare.btc.Restrictions;
import io.bitsquare.gui.util.BSFormatter;
import io.bitsquare.locale.Res;
import org.bitcoinj.core.Coin;

import javax.inject.Inject;

public class SecurityDepositValidator extends BtcValidator {

    @Inject
    public SecurityDepositValidator(BSFormatter formatter) {
        super(formatter);
        setMaxValueInBitcoin(Restrictions.MAX_SECURITY_DEPOSIT);
    }


    @Override
    public ValidationResult validate(String input) {
        ValidationResult result = validateIfNotEmpty(input);
        if (result.isValid) {
            input = cleanInput(input);
            result = validateIfNumber(input);
        }

        if (result.isValid) {
            result = validateIfNotZero(input)
                    .and(validateIfNotNegative(input))
                    .and(validateIfNotTooLowBtcValue(input))
                    .and(validateIfNotFractionalBtcValue(input))
                    .and(validateIfNotExceedsMaxBtcValue(input));
        }

        return result;
    }


    protected ValidationResult validateIfNotTooLowBtcValue(String input) {
        try {
            final Coin coin = Coin.parseCoin(input);
            Coin minSecurityDeposit = Restrictions.MIN_SECURITY_DEPOSIT;
            if (coin.compareTo(minSecurityDeposit) < 0)
                return new ValidationResult(false,
                        Res.get("validation.securityDeposit.toSmall", formatter.formatCoinWithCode(minSecurityDeposit)));
            else
                return new ValidationResult(true);
        } catch (Throwable t) {
            return new ValidationResult(false, "Invalid input: " + t.getMessage());
        }
    }
}
