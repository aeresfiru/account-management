package by.alex.account.service.dto;

import java.io.Serializable;
import java.math.BigDecimal;

public record AccountDTO(Integer id, BigDecimal balance, Boolean isBlocked, UserDTO user)
        implements Serializable {
}
