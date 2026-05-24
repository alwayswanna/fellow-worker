enum ApplicationStatus {
  PENDING,
  WITHDRAWN,
  REVIEWED,
  ACCEPTED,
  REJECTED;

  String get displayName => switch (this) {
        PENDING => 'Pending',
        WITHDRAWN => 'Withdrawn',
        REVIEWED => 'Reviewed',
        ACCEPTED => 'Accepted',
        REJECTED => 'Rejected',
      };

  bool get isActive => this == PENDING || this == REVIEWED;
}

class Application {
  final String id;
  final String vacancyId;
  final String applicantAccountId;
  final ApplicationStatus status;
  final String? createdAt;
  final String? updatedAt;

  Application({
    required this.id,
    required this.vacancyId,
    required this.applicantAccountId,
    required this.status,
    this.createdAt,
    this.updatedAt,
  });

  factory Application.fromJson(Map<String, dynamic> json) => Application(
        id: json['id'] as String,
        vacancyId: json['vacancyId'] as String,
        applicantAccountId: json['applicantAccountId'] as String,
        status: ApplicationStatus.values
            .firstWhere((e) => e.name == json['status'],
                orElse: () => ApplicationStatus.PENDING),
        createdAt: json['createdAt'] as String?,
        updatedAt: json['updatedAt'] as String?,
      );

  Map<String, dynamic> toJson() => {
        'id': id,
        'vacancyId': vacancyId,
        'applicantAccountId': applicantAccountId,
        'status': status.name,
        'createdAt': createdAt,
        'updatedAt': updatedAt,
      };
}
