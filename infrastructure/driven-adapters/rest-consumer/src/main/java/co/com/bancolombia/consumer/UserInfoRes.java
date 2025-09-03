package co.com.bancolombia.consumer;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.time.LocalDate;

public record UserInfoRes(
    @JsonProperty("userId") Long userId,
    @JsonProperty("name") String name,
    @JsonProperty("lastName") String lastName,
    @JsonProperty("email") String email,
    @JsonProperty("idDocument") String idDocument,
    @JsonProperty("phoneNumber") String phoneNumber,
    @JsonProperty("address") String address,
    @JsonProperty("birthDate") LocalDate birthDate,
    @JsonProperty("roleId") RoleId roleId,
    @JsonProperty("baseSalary") BigDecimal baseSalary
) {}