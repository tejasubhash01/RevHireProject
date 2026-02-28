import { Routes } from '@angular/router';
import { AuthGuard } from './core/guards/auth.guard';
import { RoleGuard } from './core/guards/role.guard';

export const routes: Routes = [

  {
    path: '',
    loadComponent: () =>
      import('./features/home/pages/home/home.component').then(
        (m) => m.HomeComponent
      ),
  },


  {
    path: 'about',
    loadComponent: () =>
      import('./features/about/pages/about/about.component').then(
        (m) => m.AboutComponent
      ),
  },
  {
    path: 'support',
    loadComponent: () =>
      import('./features/support/pages/support/support.component').then(
        (m) => m.SupportComponent
      ),
  },

  {
    path: 'jobs',
    loadComponent: () =>
      import('./features/jobs/pages/job-list/job-list.component').then(
        (m) => m.JobListComponent
      ),
  },
  {
    path: 'jobs/:id',
    loadComponent: () =>
      import('./features/jobs/pages/job-detail/job-detail.component').then(
        (m) => m.JobDetailComponent
      ),
  },


  {
    path: 'login',
    loadComponent: () =>
      import('./features/auth/pages/login/login.component').then(
        (m) => m.LoginComponent
      ),
  },
  {
    path: 'register',
    loadComponent: () =>
      import('./features/auth/pages/register/register.component').then(
        (m) => m.RegisterComponent
      ),
  },
  {
    path: 'forgot-password',
    loadComponent: () =>
      import(
        './features/auth/pages/forgot-password-email/forgot-password-email.component'
      ).then((m) => m.ForgotPasswordEmailComponent),
  },
  {
    path: 'forgot-password/question',
    loadComponent: () =>
      import(
        './features/auth/pages/forgot-password-question/forgot-password-question.component'
      ).then((m) => m.ForgotPasswordQuestionComponent),
  },
  {
    path: 'forgot-password/reset',
    loadComponent: () =>
      import(
        './features/auth/pages/forgot-password-reset/forgot-password-reset.component'
      ).then((m) => m.ForgotPasswordResetComponent),
  },

  {
    path: 'jobseeker',
    canActivate: [AuthGuard, RoleGuard],
    data: { role: 'JOB_SEEKER' },
    children: [
      {
        path: 'dashboard',
        loadComponent: () =>
          import(
            './features/jobseeker/pages/dashboard/dashboard.component'
          ).then((m) => m.DashboardComponent),
      },
      {
        path: 'view-profile',
        loadComponent: () =>
          import(
            './features/jobseeker/pages/view-profile/view-profile.component'
          ).then((m) => m.ViewProfileComponent),
      },
      {
        path: 'profile',
        loadComponent: () =>
          import('./features/jobseeker/pages/profile/profile.component').then(
            (m) => m.ProfileComponent
          ),
      },
      {
        path: 'resume',
        loadComponent: () =>
          import('./features/jobseeker/pages/resume/resume.component').then(
            (m) => m.ResumeComponent
          ),
      },
      {
        path: 'my-applications',
        loadComponent: () =>
          import(
            './features/jobseeker/pages/my-applications/my-applications.component'
          ).then((m) => m.MyApplicationsComponent),
      },
      {
        path: 'favourites',
        loadComponent: () =>
          import('./features/jobseeker/pages/favourites/favourites.component').then(
            (m) => m.FavouritesComponent
          ),
      },
      { path: '', redirectTo: 'dashboard', pathMatch: 'full' },
    ],
  },

  {
    path: 'employer',
    canActivate: [AuthGuard, RoleGuard],
    data: { role: 'EMPLOYER' },
    children: [
      {
        path: 'dashboard',
        loadComponent: () =>
          import('./features/employer/pages/dashboard/dashboard.component').then(
            (m) => m.DashboardComponent
          ),
      },
      {
        path: 'view-profile',
        loadComponent: () =>
          import(
            './features/employer/pages/view-profile/view-profile.component'
          ).then((m) => m.ViewProfileComponent),
      },
      {
        path: 'profile',
        loadComponent: () =>
          import('./features/employer/pages/profile/profile.component').then(
            (m) => m.ProfileComponent
          ),
      },
      {
        path: 'post-job',
        loadComponent: () =>
          import('./features/employer/pages/post-job/post-job.component').then(
            (m) => m.PostJobComponent
          ),
      },
      {
        path: 'manage-jobs',
        loadComponent: () =>
          import('./features/employer/pages/manage-jobs/manage-jobs.component').then(
            (m) => m.ManageJobsComponent
          ),
      },
      {
        path: 'jobs/:jobId/applicants',
        loadComponent: () =>
          import(
            './features/employer/pages/job-applicants/job-applicants.component'
          ).then((m) => m.JobApplicantsComponent),
      },
      { path: '', redirectTo: 'dashboard', pathMatch: 'full' },
    ],
  },


  {
    path: 'notifications',
    canActivate: [AuthGuard],
    loadComponent: () =>
      import(
        './features/notifications/pages/notifications-list/notifications-list.component'
      ).then((m) => m.NotificationsListComponent),
  },


  {
    path: 'admin',
    canActivate: [AuthGuard, RoleGuard],
    data: { role: 'ADMIN' },
    children: [
      {
        path: 'dashboard',
        loadComponent: () =>
          import('./features/admin/pages/dashboard/dashboard.component').then(
            (m) => m.DashboardComponent
          ),
      },
      {
        path: 'enums',
        loadComponent: () =>
          import('./features/admin/pages/enums/enums.component').then(
            (m) => m.EnumsComponent
          ),
      },
      { path: '', redirectTo: 'dashboard', pathMatch: 'full' },
    ],
  },


  { path: '**', redirectTo: '/login' },
];
