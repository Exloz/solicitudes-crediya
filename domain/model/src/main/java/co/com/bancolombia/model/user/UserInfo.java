package co.com.bancolombia.model.user;

import java.math.BigDecimal;
import java.time.LocalDate;

public record UserInfo(
    Long userId,
    String name,
    String lastName,
    String email,
    String idDocument,
    String phoneNumber,
    String address,
    LocalDate birthDate,
    String roleId,
    BigDecimal baseSalary
) {}