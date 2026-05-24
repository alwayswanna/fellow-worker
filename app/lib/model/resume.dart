class WorkExperience {
  final String company;
  final String position;
  final String? startDate;
  final String? endDate;
  final String? description;

  WorkExperience({
    required this.company,
    required this.position,
    this.startDate,
    this.endDate,
    this.description,
  });

  factory WorkExperience.fromJson(Map<String, dynamic> json) => WorkExperience(
        company: json['company'] as String,
        position: json['position'] as String,
        startDate: json['startDate'] as String?,
        endDate: json['endDate'] as String?,
        description: json['description'] as String?,
      );

  Map<String, dynamic> toJson() => {
        'company': company,
        'position': position,
        if (startDate != null) 'startDate': startDate,
        if (endDate != null) 'endDate': endDate,
        if (description != null) 'description': description,
      };
}

class Education {
  final String institution;
  final String degree;
  final String fieldOfStudy;
  final String? startDate;
  final String? endDate;

  Education({
    required this.institution,
    required this.degree,
    required this.fieldOfStudy,
    this.startDate,
    this.endDate,
  });

  factory Education.fromJson(Map<String, dynamic> json) => Education(
        institution: json['institution'] as String,
        degree: json['degree'] as String,
        fieldOfStudy: json['fieldOfStudy'] as String,
        startDate: json['startDate'] as String?,
        endDate: json['endDate'] as String?,
      );

  Map<String, dynamic> toJson() => {
        'institution': institution,
        'degree': degree,
        'fieldOfStudy': fieldOfStudy,
        if (startDate != null) 'startDate': startDate,
        if (endDate != null) 'endDate': endDate,
      };
}

class Resume {
  final String id;
  final String firstName;
  final String lastName;
  final String email;
  final String? phone;
  final String? desiredPosition;
  final String? summary;
  final String? birthDate;
  final String? photoUrl;
  final List<String> skills;
  final List<WorkExperience> experience;
  final List<Education> education;
  final List<String> links;
  final String? createdAt;
  final String? updatedAt;

  Resume({
    required this.id,
    required this.firstName,
    required this.lastName,
    required this.email,
    this.phone,
    this.desiredPosition,
    this.summary,
    this.birthDate,
    this.photoUrl,
    required this.skills,
    required this.experience,
    required this.education,
    required this.links,
    this.createdAt,
    this.updatedAt,
  });

  String get fullName => '$firstName $lastName';

  factory Resume.fromJson(Map<String, dynamic> json) => Resume(
        id: json['id'] as String,
        firstName: json['firstName'] as String,
        lastName: json['lastName'] as String,
        email: json['email'] as String,
        phone: json['phone'] as String?,
        desiredPosition: json['desiredPosition'] as String?,
        summary: json['summary'] as String?,
        birthDate: json['birthDate'] as String?,
        photoUrl: json['photoUrl'] as String?,
        skills: (json['skills'] as List<dynamic>?)
                ?.map((e) => e as String)
                .toList() ??
            [],
        experience: (json['experience'] as List<dynamic>?)
                ?.map((e) => WorkExperience.fromJson(e as Map<String, dynamic>))
                .toList() ??
            [],
        education: (json['education'] as List<dynamic>?)
                ?.map((e) => Education.fromJson(e as Map<String, dynamic>))
                .toList() ??
            [],
        links: (json['links'] as List<dynamic>?)
                ?.map((e) => e as String)
                .toList() ??
            [],
        createdAt: json['createdAt'] as String?,
        updatedAt: json['updatedAt'] as String?,
      );
}
