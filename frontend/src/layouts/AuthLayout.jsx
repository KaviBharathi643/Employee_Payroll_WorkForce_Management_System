import { Outlet } from 'react-router-dom';

export default function AuthLayout() {
  return (
    <div className="min-h-screen w-full bg-background flex flex-col lg:flex-row">
      {/* Left Column: Branding (hidden on mobile/tablet) */}
      <div className="hidden lg:flex lg:w-1/2 flex-col justify-between p-12 xl:p-16 bg-gradient-to-br from-background to-secondary/80 relative overflow-hidden">
        {/* Background blobs / circles */}
        <div className="absolute top-[-20%] right-[-25%] w-[80%] h-[80%] rounded-full bg-white opacity-40 blur-3xl pointer-events-none"></div>
        <div className="absolute bottom-[-10%] left-[-10%] w-[50%] h-[50%] rounded-full bg-secondary/50 blur-3xl pointer-events-none"></div>
        
        {/* Logo Header */}
        <div className="flex items-center gap-3">
          <div className="flex h-10 w-10 items-center justify-center rounded-xl bg-primary shadow-md shadow-primary/30">
            <svg className="h-6 w-6 text-white" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2.5" strokeLinecap="round" strokeLinejoin="round">
              <path d="M3 9l9-7 9 7v11a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2z" />
              <polyline points="9 22 9 12 15 12 15 22" />
            </svg>
          </div>
          <div>
            <h2 className="text-xl font-extrabold text-primary-dark tracking-tight leading-none">WorkSphere</h2>
            <span className="text-[10px] font-bold text-text-main/70 uppercase tracking-widest mt-1 block">Payroll & Workforce Management</span>
          </div>
        </div>

        {/* Hero Section */}
        <div className="my-auto space-y-6">
          <div className="inline-flex items-center gap-1.5 rounded-full bg-secondary border border-primary/20 px-3.5 py-1.5 text-xs font-semibold text-primary-dark shadow-sm">
            <span className="flex h-2 w-2 rounded-full bg-primary animate-pulse"></span>
            Smarter Workforce. Better Future.
          </div>
          
          <h1 className="text-4xl xl:text-5xl font-extrabold tracking-tight text-slate-900 leading-tight">
            Managing People.<br />
            <span className="text-primary">Powering Performance.</span>
          </h1>
          
          <p className="text-slate-600 text-sm xl:text-base max-w-md leading-relaxed">
            WorkSphere helps you streamline payroll, simplify processes, and empower your workforce — all in one powerful platform.
          </p>

          {/* Features Card */}
          <div className="bg-card/85 backdrop-blur-md rounded-2xl p-6 shadow-sm border border-primary/25 space-y-5 max-w-md">
            <div className="flex items-start gap-4">
              <div className="p-2.5 bg-secondary rounded-xl text-primary">
                <svg className="h-5 w-5" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth="2">
                  <path strokeLinecap="round" strokeLinejoin="round" d="M17 20h5v-2a3 3 0 00-5.356-1.857M17 20H7m10 0v-2c0-.656-.126-1.283-.356-1.857M7 20H2v-2a3 3 0 015.356-1.857M7 20v-2c0-.656.126-1.283.356-1.857m0 0a5.002 5.002 0 019.288 0M15 7a3 3 0 11-6 0 3 3 0 016 0zm6 3a2 2 0 11-4 0 2 2 0 014 0zM7 10a2 2 0 11-4 0 2 2 0 014 0z" />
                </svg>
              </div>
              <div>
                <h4 className="font-bold text-slate-900 text-sm">Smart Workforce</h4>
                <p className="text-xs text-slate-500 mt-0.5">Effortlessly manage employees and organizational data.</p>
              </div>
            </div>
            
            <div className="flex items-start gap-4">
              <div className="p-2.5 bg-secondary rounded-xl text-primary">
                <svg className="h-5 w-5" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth="2">
                  <path strokeLinecap="round" strokeLinejoin="round" d="M9 19v-6a2 2 0 00-2-2H5a2 2 0 00-2 2v6a2 2 0 002 2h2a2 2 0 002-2zm0 0V9a2 2 0 012-2h2a2 2 0 012 2v10m-6 0a2 2 0 002 2h2a2 2 0 002-2m0 0V5a2 2 0 012-2h2a2 2 0 012 2v14a2 2 0 002 2h2a2 2 0 002-2z" />
                </svg>
              </div>
              <div>
                <h4 className="font-bold text-slate-900 text-sm">Accurate Payroll</h4>
                <p className="text-xs text-slate-500 mt-0.5">Automate payroll processes with accuracy & compliance.</p>
              </div>
            </div>
            
            <div className="flex items-start gap-4">
              <div className="p-2.5 bg-secondary rounded-xl text-primary">
                <svg className="h-5 w-5" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth="2">
                  <path strokeLinecap="round" strokeLinejoin="round" d="M9 12l2 2 4-4m5.618-4.016A11.955 11.955 0 0112 2.944a11.955 11.955 0 01-8.618 3.04A12.02 12.02 0 003 9c0 5.591 3.824 10.29 9 11.622 5.176-1.332 9-6.03 9-11.622 0-1.042-.133-2.052-.382-3.016z" />
                </svg>
              </div>
              <div>
                <h4 className="font-bold text-slate-900 text-sm">Secure & Reliable</h4>
                <p className="text-xs text-slate-500 mt-0.5">Enterprise-grade security to protect your critical data.</p>
              </div>
            </div>
          </div>
        </div>

        {/* Footer/Branding text */}
        <div className="text-xs text-slate-400">
          &copy; {new Date().getFullYear()} WorkSphere. All rights reserved.
        </div>
      </div>

      {/* Right Column: Form Container */}
      <div className="w-full lg:w-1/2 flex flex-col justify-center items-center p-6 sm:p-12 md:p-16 relative bg-background">
        <div className="absolute top-1/2 left-1/2 -translate-x-1/2 -translate-y-1/2 w-[70%] h-[70%] bg-secondary/30 rounded-full blur-3xl pointer-events-none"></div>
        
        {/* Mobile/Tablet logo display */}
        <div className="lg:hidden flex items-center gap-2 mb-8">
          <div className="flex h-8 w-8 items-center justify-center rounded-lg bg-primary">
            <svg className="h-5 w-5 text-white" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2.5" strokeLinecap="round" strokeLinejoin="round">
              <path d="M3 9l9-7 9 7v11a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2z" />
              <polyline points="9 22 9 12 15 12 15 22" />
            </svg>
          </div>
          <h2 className="text-lg font-bold text-slate-900 tracking-tight leading-none">WorkSphere</h2>
        </div>
        
        {/* Outlet wrapper card */}
        <div className="w-full max-w-[480px] bg-card rounded-3xl p-8 sm:p-10 shadow-xl border border-primary/20 z-10">
          <Outlet />
        </div>
      </div>
    </div>
  );
}
