import 'package:flutter_bloc/flutter_bloc.dart';

import '../../service/vacancy_api_service.dart';
import 'saved_vacancies_state.dart';

class SavedVacanciesCubit extends Cubit<SavedVacanciesState> {
  final VacancyApiService _vacancyApiService;

  SavedVacanciesCubit(this._vacancyApiService) : super(const SavedVacanciesState());

  Future<void> load() async {
    emit(state.copyWith(status: SavedVacanciesStatus.loading));
    final vacancies = await _vacancyApiService.getSavedVacancies();
    emit(state.copyWith(
      status: SavedVacanciesStatus.loaded,
      vacancies: vacancies,
    ));
  }
}
