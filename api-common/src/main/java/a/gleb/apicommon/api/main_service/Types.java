package a.gleb.apicommon.api.main_service;

public class Types {
    public enum EmploymentTypeEnumeration {
        FULL_TIME, PART_TIME, CONTRACT, TEMPORARY, INTERNSHIP, FREELANCE
    }

    public enum WorkScheduleEnumeration {
        FULL_DAY, SHIFT_WORK, FLEXIBLE, REMOTE, ROTATING
    }

    public enum ExperienceLevelEnumeration {
        INTERN, JUNIOR, MIDDLE, SENIOR, LEAD, DIRECTOR
    }

    public enum Currency {
        USD, EUR, GBP, JPY, CNY, RUB, UAH, KZT,
        CAD, AUD, CHF, PLN, TRY, INR, BRL
    }
}
