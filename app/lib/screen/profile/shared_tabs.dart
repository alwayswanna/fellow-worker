// Widgets shared by CompanyOwnerPanel and RecruiterPanel — factored into
// their own files under shared_tabs/ and re-exported here as a single
// import path for both panels, avoiding a circular import between them.
export 'shared_tabs/applications_tab.dart' show RecruiterApplicationsTab;
export 'shared_tabs/vacancies_tab.dart' show VacanciesTab;
