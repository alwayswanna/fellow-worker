/// Safe enum parsing — returns null instead of throwing on unknown values.
extension EnumByName<T extends Enum> on Iterable<T> {
  T? tryByName(String? name) {
    if (name == null) return null;
    for (final v in this) {
      if (v.name == name) return v;
    }
    return null;
  }
}

enum CompanySize {
  STARTUP,
  SMALL,
  MEDIUM,
  LARGE,
  ENTERPRISE;

  String get displayName => switch (this) {
        STARTUP => 'Startup',
        SMALL => 'Small',
        MEDIUM => 'Medium',
        LARGE => 'Large',
        ENTERPRISE => 'Enterprise',
      };
}

enum EmploymentType {
  FULL_TIME,
  PART_TIME,
  CONTRACT,
  INTERNSHIP,
  FREELANCE;

  String get displayName => switch (this) {
        FULL_TIME => 'Full-time',
        PART_TIME => 'Part-time',
        CONTRACT => 'Contract',
        INTERNSHIP => 'Internship',
        FREELANCE => 'Freelance',
      };
}

enum WorkFormat {
  OFFICE,
  REMOTE,
  HYBRID;

  String get displayName => switch (this) {
        OFFICE => 'Office',
        REMOTE => 'Remote',
        HYBRID => 'Hybrid',
      };
}

enum ExperienceLevel {
  NO_EXPERIENCE,
  JUNIOR,
  MIDDLE,
  SENIOR,
  LEAD;

  String get displayName => switch (this) {
        NO_EXPERIENCE => 'No experience',
        JUNIOR => 'Junior',
        MIDDLE => 'Middle',
        SENIOR => 'Senior',
        LEAD => 'Lead',
      };
}

enum VacancyStatus {
  DRAFT,
  ACTIVE,
  CLOSED;

  String get displayName => switch (this) {
        DRAFT => 'Draft',
        ACTIVE => 'Active',
        CLOSED => 'Closed',
      };
}

class CompanyRecruiter {
  final String id;
  final String companyId;
  final String accountId;
  final String? joinedAt;

  CompanyRecruiter({
    required this.id,
    required this.companyId,
    required this.accountId,
    this.joinedAt,
  });

  factory CompanyRecruiter.fromJson(Map<String, dynamic> json) => CompanyRecruiter(
        id: json['id'] as String,
        companyId: json['companyId'] as String,
        accountId: json['accountId'] as String,
        joinedAt: json['joinedAt'] as String?,
      );
}

class CompanyShort {
  final String id;
  final String name;
  final String? logoUrl;
  final String? city;
  final double rating;

  CompanyShort({
    required this.id,
    required this.name,
    this.logoUrl,
    this.city,
    required this.rating,
  });

  factory CompanyShort.fromJson(Map<String, dynamic> json) => CompanyShort(
        id: json['id'] as String,
        name: json['name'] as String,
        logoUrl: json['logoUrl'] as String?,
        city: json['city'] as String?,
        rating: (json['rating'] as num).toDouble(),
      );
}

class Vacancy {
  final String id;
  final String companyId;
  final CompanyShort? company;
  final String title;
  final String? description;
  final String? requirements;
  final int? salaryFrom;
  final int? salaryTo;
  final String? currency;
  final EmploymentType? employmentType;
  final WorkFormat? workFormat;
  final ExperienceLevel? experienceLevel;
  final String? city;
  final String? country;
  final VacancyStatus status;
  final List<String> skills;
  final String? createdAt;
  final String? updatedAt;
  final String? contactName;
  final String? contactEmail;
  final String? contactPhone;

  Vacancy({
    required this.id,
    required this.companyId,
    this.company,
    required this.title,
    this.description,
    this.requirements,
    this.salaryFrom,
    this.salaryTo,
    this.currency,
    this.employmentType,
    this.workFormat,
    this.experienceLevel,
    this.city,
    this.country,
    required this.status,
    required this.skills,
    this.createdAt,
    this.updatedAt,
    this.contactName,
    this.contactEmail,
    this.contactPhone,
  });

  factory Vacancy.fromJson(Map<String, dynamic> json) => Vacancy(
        id: json['id'] as String,
        companyId: (json['companyId'] ?? json['company']?['id'] ?? '') as String,
        company: json['company'] != null
            ? CompanyShort.fromJson(json['company'] as Map<String, dynamic>)
            : null,
        title: json['title'] as String,
        description: json['description'] as String?,
        requirements: json['requirements'] as String?,
        salaryFrom: (json['salaryFrom'] as num?)?.toInt(),
        salaryTo: (json['salaryTo'] as num?)?.toInt(),
        currency: json['currency'] as String?,
        employmentType:
            EmploymentType.values.tryByName(json['employmentType'] as String?),
        workFormat:
            WorkFormat.values.tryByName(json['workFormat'] as String?),
        experienceLevel:
            ExperienceLevel.values.tryByName(json['experienceLevel'] as String?),
        city: json['city'] as String?,
        country: json['country'] as String?,
        status: VacancyStatus.values.tryByName(json['status'] as String?) ??
            VacancyStatus.ACTIVE,
        skills: (json['skills'] as List<dynamic>?)
                ?.map((e) => e as String)
                .toList() ??
            [],
        createdAt: json['createdAt'] as String?,
        updatedAt: json['updatedAt'] as String?,
        contactName: json['contactName'] as String?,
        contactEmail: json['contactEmail'] as String?,
        contactPhone: json['contactPhone'] as String?,
      );
}

class Company {
  final String id;
  final String name;
  final String? description;
  final String? website;
  final String? logoUrl;
  final String? industry;
  final CompanySize? size;
  final String? city;
  final String? country;
  final double rating;
  final int reviewCount;

  Company({
    required this.id,
    required this.name,
    this.description,
    this.website,
    this.logoUrl,
    this.industry,
    this.size,
    this.city,
    this.country,
    required this.rating,
    required this.reviewCount,
  });

  factory Company.fromJson(Map<String, dynamic> json) => Company(
        id: json['id'] as String,
        name: json['name'] as String,
        description: json['description'] as String?,
        website: json['website'] as String?,
        logoUrl: json['logoUrl'] as String?,
        industry: json['industry'] as String?,
        size: CompanySize.values.tryByName(json['size'] as String?),
        city: json['city'] as String?,
        country: json['country'] as String?,
        rating: (json['rating'] as num).toDouble(),
        reviewCount: json['reviewCount'] as int? ?? 0,
      );
}
