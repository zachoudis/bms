package com.bms.triangle.core.model;

import com.bms.triangle.core.enums.ValidationReason;

/**
 * One invalid side.
 *
 * @param value the side exactly as given
 */
public record ValidationError(
    String value, 
    ValidationReason reason
) {
}
