import 'package:flutter_bloc/flutter_bloc.dart';

import '../../model/application.dart';
import '../../model/vacancy.dart';
import '../../service/vacancy_api_service.dart';
import 'applications_state.dart';

class ApplicationsCubit extends Cubit<ApplicationsState> {
  final VacancyApiService _vacancyApiService;

  ApplicationsCubit(this._vacancyApiService) : super(const ApplicationsState());

  Future<void> load() async {
    emit(state.copyWith(status: ApplicationsStatus.loading));
    final applications = await _vacancyApiService.getMyApplications();

    final vacancyIds = applications.map((a) => a.vacancyId).toSet();
    final vacancies = await Future.wait(
      vacancyIds.map((id) => _vacancyApiService.getVacancyById(id)),
    );
    final vacanciesById = <String, Vacancy>{
      for (final v in vacancies)
        if (v != null) v.id: v,
    };

    emit(state.copyWith(
      status: ApplicationsStatus.loaded,
      applications: applications,
      vacanciesById: vacanciesById,
    ));
  }

  Future<void> withdraw(Application application) async {
    final ok = await _vacancyApiService.withdrawApplication(application.vacancyId);
    if (ok) {
      emit(state.copyWith(
        applications: state.applications
            .where((a) => a.id != application.id)
            .toList(),
      ));
    }
  }
}
