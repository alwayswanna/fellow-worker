import 'package:equatable/equatable.dart';

import '../../model/application.dart';
import '../../model/vacancy.dart';

enum ApplicationsStatus { initial, loading, loaded, error }

class ApplicationsState extends Equatable {
  final ApplicationsStatus status;
  final List<Application> applications;
  final Map<String, Vacancy> vacanciesById;

  const ApplicationsState({
    this.status = ApplicationsStatus.initial,
    this.applications = const [],
    this.vacanciesById = const {},
  });

  bool get isLoading => status == ApplicationsStatus.loading;

  ApplicationsState copyWith({
    ApplicationsStatus? status,
    List<Application>? applications,
    Map<String, Vacancy>? vacanciesById,
  }) {
    return ApplicationsState(
      status: status ?? this.status,
      applications: applications ?? this.applications,
      vacanciesById: vacanciesById ?? this.vacanciesById,
    );
  }

  @override
  List<Object?> get props => [status, applications, vacanciesById];
}
