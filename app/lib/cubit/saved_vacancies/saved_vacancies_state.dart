import 'package:equatable/equatable.dart';

import '../../model/vacancy.dart';

enum SavedVacanciesStatus { initial, loading, loaded, error }

class SavedVacanciesState extends Equatable {
  final SavedVacanciesStatus status;
  final List<Vacancy> vacancies;

  const SavedVacanciesState({
    this.status = SavedVacanciesStatus.initial,
    this.vacancies = const [],
  });

  bool get isLoading => status == SavedVacanciesStatus.loading;

  SavedVacanciesState copyWith({
    SavedVacanciesStatus? status,
    List<Vacancy>? vacancies,
  }) {
    return SavedVacanciesState(
      status: status ?? this.status,
      vacancies: vacancies ?? this.vacancies,
    );
  }

  @override
  List<Object?> get props => [status, vacancies];
}
