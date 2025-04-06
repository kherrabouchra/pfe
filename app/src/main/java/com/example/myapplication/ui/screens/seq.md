
  \subsection{Sequence Diagrams}
  Login/Signup
      \subsection{Specification of use cases}

\subsubsection{Login}
\begin{itemize}
\item \textbf{Descriptive sheet}
\end{itemize}

\begin{table}[h]
\caption{Login descriptive sheet}
\begin{tabular}{|p{3cm}|p{9cm}|}
\hline
\textbf{Use case} & Login \\
\hline
\textbf{Type} & Principal \\
\hline
\textbf{End-objective} & Authenticates users to grant them access to the platform. \\
\hline
\textbf{Actor} & User \\
\hline
\textbf{Pre-condition} & 
\begin{itemize}
\item The user must have a valid Google account.
\item The application must be installed on their device.
\end{itemize} \\
\hline
\textbf{Basic flow} & 
\begin{enumerate}
\item The user clicks on "Login with Google".
\item The system redirects to Google authentication.
\item The user provides their credentials to Google.
\item Google authenticates the user and returns token.
\item The system retrieves the user profile.
\item The system displays the personalized dashboard.
\end{enumerate} \\
\hline
\textbf{Alternative flow} & 
\textbf{A1: Authentication fails.} Step 4 of basic flow:
\begin{enumerate}
\item[5.] The system displays the message "Authentication failed. Please try again."
\end{enumerate}
The basic flow resumes at step 2. \\
\hline
\textbf{Exceptional flow} & 
\textbf{E1: Network connection error.} Step 2 of basic flow:
\begin{enumerate}
\item[5.] The system displays the message "Network error. Please check your connection and try again."
\end{enumerate} \\
\hline
\textbf{Post-condition} & The user is logged in and has access to their account services. \\
\hline
\end{tabular}
\end{table}

\subsubsection{Sign Up}
\begin{itemize}
\item \textbf{Descriptive sheet}
\end{itemize}

\begin{table}[h]
\caption{Sign Up descriptive sheet}
\begin{tabular}{|p{3cm}|p{9cm}|}
\hline
\textbf{Use case} & Sign Up \\
\hline
\textbf{Type} & Principal \\
\hline
\textbf{End-objective} & Creates a new user account with personalized preferences. \\
\hline
\textbf{Actor} & User  \\
\hline
\textbf{Pre-condition} & 
\begin{itemize}
\item The user must have a valid Google account.
\item The application must be installed on their device.
\end{itemize} \\
\hline
\textbf{Basic flow} & 
\begin{enumerate}
\item The user clicks on "Sign up with Google".
\item The system redirects to Google authentication.
\item The user provides their credentials to Google.
\item Google returns authentication token and basic profile data.
\item The system creates a user account.
\item The system confirms account creation.
\item The system presents an initial questionnaire.
\item The user submits questionnaire responses.
\item The system stores user preferences and profile data.
\item The system processes user profile and questionnaire data.
\item The system displays personalized interface based on preferences.
\end{enumerate} \\
\hline
\textbf{Alternative flow} & 
\textbf{A1: User already exists.} Step 5 of basic flow:
\begin{enumerate}
\item[5.] The system detects an existing account.
\item[6.] The system displays the message "Account already exists. Please login."
\end{enumerate}
The use case ends. \\
\hline
\textbf{Exceptional flow} & 
\textbf{E1: Questionnaire submission fails.} Step 8 of basic flow:
\begin{enumerate}
\item[9.] The system displays the message "Failed to save preferences. Please try again."
\end{enumerate} \\
\hline
\textbf{Post-condition} & A new user account is created with personalized preferences. \\
\hline
\end{tabular}
\end{table}

\subsubsection{Fall Detection}
\begin{itemize}
\item \textbf{Descriptive sheet}
\end{itemize}

\begin{table}[h]
\caption{Fall Detection descriptive sheet}
\begin{tabular}{|p{3cm}|p{9cm}|}
\hline
\textbf{Use case} & Fall Detection \\
\hline
\textbf{Type} & Principal \\
\hline
\textbf{End-objective} & Detects falls and alerts emergency contacts when necessary. \\
\hline
\textbf{Primary actor} & User \\
\hline
\textbf{Secondary \newline actor} & Emergency Contact \\
\hline
\textbf{Pre-condition} & 
\begin{itemize}
\item The user must have activated fall detection.
\item The user must have set up emergency contacts.
\end{itemize} \\
\hline
\textbf{Basic flow} & 
\begin{enumerate}
\item The user activates fall detection.
\item The system confirms activation.
\item The system continuously monitors for falls.
\item A fall is detected.
\item The system alerts the user with a one minute countdown.
\item The user does not respond within the countdown period.
\item The system sends an emergency notification with user data to all emergency contacts.
\item The emergency contact provides assistance.
\item The system records the incident.
\end{enumerate} \\
\hline
\textbf{Alternative flow} & 
\textbf{A1: User cancels alert.} Step 6 of basic flow:
\begin{enumerate}
\item[6.] The user cancels the alert.
\item[7.] The system ends the protocol.
\item[8.] The system records the false alarm.
\end{enumerate} \\
 
\hline
\textbf{Post-condition} & The incident is recorded and appropriate assistance is provided if needed. \\
\hline
\end{tabular}
\end{table}

\subsubsection{View Activities}
\begin{itemize}
\item \textbf{Descriptive sheet}
\end{itemize}
\newpage
\begin{longtable}{|p{3cm}|p{9cm}|}
\caption{View Activities descriptive sheet} \\
\hline
\textbf{Use case} & View Activities \\
\hline
\endfirsthead
\hline
\textbf{Use case} & View Activities \\
\hline
\endhead
\hline
\textbf{Type} & Principal \\
\hline
\textbf{End-objective} & Allows users to view and manage their health-related activities. \\
\hline
\textbf{Actor} & User \\
\hline
\textbf{Pre-condition} & 
\begin{itemize}
\item The user must be logged in. 
\end{itemize} \\
\hline
\textbf{Basic flow} & 
\begin{enumerate}
\item The user accesses the activities list.
\item The system retrieves user's activity categories.
\item The system displays activity categories.
\item The user selects a specific activity category (nutrition, sleep, water intake, medication, or mobility).
\item The system retrieves the selected category data.
\item The system displays the category summary and history.
\item The user views the detailed information. 
\end{enumerate} \\
\hline
\textbf{Alternative flow} & 
\textbf{A1: Record new activity data.} Step 7 of basic flow:
\begin{enumerate}
\item[7.] The user selects to record new data.
\item[8.] The system displays the appropriate input form.
\item[9.] The user submits the information.
\item[10.] The system stores the data.
\item[11.] The system confirms the data has been recorded.
\end{enumerate}
The basic flow resumes at step 7. \\
\hline
& \textbf{A2: Select activities to track.} Step 4 of basic flow:
\begin{enumerate}
\item[4.] The user selects "Edit tracked activities" from the activities page.
\item[5.] The system displays a list of available health activities with toggle options (nutrition, sleep, water intake, medication, mobility).
\item[6.] The user toggles on/off specific activities they wish to track.
\item[7.] The user confirms their selection.
\item[8.] The system updates user preferences for tracked activities.
\item[9.] The system refreshes the activities list to display only the selected activities.
\end{enumerate}
The basic flow resumes at step 4 with the updated activity selection. \\
\hline
\textbf{Exceptional flow} & 
\textbf{E1: No data available.} Step 6 of basic flow:
\begin{enumerate}
\item[3.] The system displays a message "No activity data available yet."
\end{enumerate}
The use case ends. \\
\hline
\textbf{Post-condition} & The user has viewed or managed their health activity data. \\
\hline
\end{longtable}

\subsubsection{Manage User Profile}
\begin{itemize}
\item \textbf{Descriptive sheet}
\end{itemize}
\newpage
\begin{longtable}{|p{3cm}|p{9cm}|}
\caption{Manage User Profile descriptive sheet} \\
\hline
\textbf{Use case} & Manage User Profile \\
\hline
\endfirsthead
\hline
\textbf{Use case} & Manage User Profile \\
\hline
\endhead
\hline
\textbf{Type} & Principal \\
\hline
\textbf{End-objective} & Allows users to view and update their profile information and settings. \\
\hline
\textbf{Actor} & User, System \\
\hline
\textbf{Pre-condition} & 
\begin{itemize}
\item The user must be logged in.
\end{itemize} \\
\hline
\textbf{Basic flow} & 
\begin{enumerate}
\item The user accesses account settings.
\item The system retrieves user profile data.
\item The system displays the user profile.
\item The user selects an option (update or delete account, manage reminders, manage permissions, manage emergency contacts or About).
\item The system displays the appropriate form or options.
\item The user makes changes and submits.
\item The system validates the input.
\item The system updates the profile data.
\item The system confirms the changes.
\end{enumerate} \\
\hline
\textbf{Alternative flow} & 
\textbf{A1: Manage emergency contacts.} Step 4 of basic flow:
\begin{enumerate}
\item[5.] The user selects manage emergency contacts.
\item[6.] The system retrieves emergency contact information.
\item[7.] The system displays emergency contact options.
\item[8.] The user selects an action (view, add, update, or delete).
\item[9.] The system processes the selected action.
\item[10.] The system confirms the action completion.
\end{enumerate}
The basic flow resumes at step 9. \\
& \textbf{A6: Update profile information.} Step 3 of basic flow:
\begin{enumerate}
\item[4.] The user selects update profile information.
\item[5.] The system retrieves current profile information.
\item[6.] The system displays editable profile form with current information (name, age, gender, health conditions, allergies, height, weight).
\item[7.] The user modifies profile information.
\item[8.] The user submits updated information.
\item[9.] The system validates the input.
\item[10.] The system stores the updated profile information.
\item[11.] The system confirms profile information update.
\end{enumerate}
The basic flow resumes at step 9. \\
& \textbf{A2: Delete account.} Step 4 of basic flow:
\begin{enumerate}
\item[5.] The user selects delete account.
\item[6.] The system requests confirmation.
\item[7.] The user confirms deletion.
\item[8.] The system processes account deletion.
\item[9.] The system confirms account deletion.
\end{enumerate}
The use case ends. \\

& \textbf{A3: Manage reminders.} Step 4 of basic flow:
\begin{enumerate}
\item[5.] The user selects manage reminders.
\item[6.] The system retrieves reminder settings.
\item[7.] The system displays reminder options with toggle buttons.
\item[8.] The user toggles reminder on/off (water/nutrition/sleep/medication/steps/hygiene).
\item[9.] The system updates reminder status.
\item[10.] The system confirms toggle status change.
\end{enumerate}
The basic flow resumes at step 9. \\

& \textbf{A4: Manage permissions.} Step 4 of basic flow:
\begin{enumerate}
\item[5.] The user selects manage permissions.
\item[6.] The system retrieves current permission settings.
\item[7.] The system displays permission options.
\item[8.] The user modifies permission settings.
\item[9.] The system updates permission settings.
\item[10.] The system confirms permission changes.
\end{enumerate}
The basic flow resumes at step 9. \\

& \textbf{A5: View About information.} Step 4 of basic flow:
\begin{enumerate}
\item[5.] The user selects About.
\item[6.] The system displays application information, version, and developer details.
\item[7.] The user views the information.
\end{enumerate}
The basic flow resumes at step 9. \\

\hline
\textbf{Exceptional flow} & 
\textbf{E1: Validation fails.} Step 7 of basic flow:
\begin{enumerate}
\item[8.] The system displays error messages for invalid inputs.
\item[9.] The user corrects the inputs and resubmits.
\end{enumerate}
The basic flow resumes at step 7. \\
\hline
\textbf{Post-condition} & The user profile is updated according to the user's changes. \\
\hline
\end{longtable}


\subsubsection{AI Assistance Chat}
\begin{itemize}
\item \textbf{Descriptive sheet}
\end{itemize}

\begin{table}[h]
\caption{AI Assistance Chat descriptive sheet}
\begin{tabular}{|p{3cm}|p{9cm}|}
\hline
\textbf{Use case} & AI Assistance Chat \\
\hline
\textbf{Type} & Principal \\
\hline
\textbf{End-objective} & Provides users with AI-powered assistance and information. \\
\hline
\textbf{Actor} & User, System, AI Assistant \\
\hline
\textbf{Pre-condition} & 
\begin{itemize}
\item The user must be logged in.
\item The system must have internet connectivity.
\end{itemize} \\
\hline
\textbf{Basic flow} & 
\begin{enumerate}
\item The user opens the AI assistance chat.
\item The system initializes the AI assistant.
\item The system displays the chat interface.
\item The user sends a message or question.
\item The system processes the user input.
\item The AI generates a response.
\item The system displays the AI response.
\item Steps 4-7 repeat until the user exits the chat.
\end{enumerate} \\
\hline
\textbf{Alternative flow} & 
\textbf{A1: User requests specific health information.} Step 4 of basic flow:
\begin{enumerate}
\item[5.] The user requests specific health information.
\item[6.] The system retrieves relevant user health data.
\item[7.] The AI generates a personalized response incorporating the health data.
\item[8.] The system displays the personalized response.
\end{enumerate}
The basic flow resumes at step 4. \\
\hline
\textbf{Exceptional flow} & 
\textbf{E1: API connection fails.} Step 5 of basic flow:
\begin{enumerate}
\item[6.] The system displays an error message "Unable to connect to AI service. Please try again later."
\end{enumerate}
The use case ends. \\
\hline
\textbf{Post-condition} & The user receives AI-powered assistance for their queries. \\
\hline
\end{tabular}
\end{table}
\newpage
\begin{longtable}{|p{3cm}|p{9cm}|}
\caption{View Health Dashboard descriptive sheet} \\
\hline
\textbf{Use case} & View Health Dashboard \\
\hline
\endfirsthead
\hline
\textbf{Use case} & View Health Dashboard \\
\hline
\endhead
\hline
\textbf{Type} & Principal \\
\hline
\textbf{End-objective} & Allows users to view a comprehensive overview of their health status and access detailed health information. \\
\hline
\textbf{Actor} & User \\
\hline
\textbf{Pre-condition} & 
\begin{itemize}
\item The user must be logged in. 
\end{itemize} \\
\hline
\textbf{Basic flow} & 
\begin{enumerate}
\item The user accesses the health dashboard.
\item The system retrieves dashboard data.
\item The system processes the dashboard data.
\item The system displays the user's health summary and custom recommendations.
\item The user selects a dashboard option (health recommendations, manage vitals, or mood and symptoms).
\item The system retrieves the relevant data.
\item The system displays the selected information. 
\end{enumerate} \\
\hline
\textbf{Alternative flow} & 
\textbf{A1: View Health Score.} Step 5 of basic flow:
\begin{enumerate}
\item[6.] The user selects health score.
\item[7.] The system retrieves health score data.
\item[8.] The system calculates and formats the health score.
\item[9.] The system displays the health score with breakdown.
\item[10.] The user may select to view score history.
\end{enumerate}
The basic flow resumes at step 8. \\
 

& \textbf{A2: View Health Recommendations.} Step 5 of basic flow:
\begin{enumerate}
\item[6.] The user may select a recommendation for details or save a recommendation.
\item[7.] The system displays recommendation details.
\end{enumerate}
The basic flow resumes at step 8. \\

& \textbf{A3: Manage Vitals.} Step 5 of basic flow:
\begin{enumerate}
\item[6.] The user selects manage vitals.
\item[7.] The system retrieves vitals data.
\item[8.] The system processes vitals data.
\item[9.] The system displays vitals overview.
\item[10.] The user may select to record a new vital or view vital history.
\end{enumerate}
The basic flow resumes at step 8. \\

&\textbf{A5: Manage Mood.} Step 5 of basic flow:
\begin{enumerate} 
\item[6.] The system displays mood overview and trends.
\item[7.] The user may select to record new mood entry or view mood history.
\item[8.] The system saves the mood state.
\end{enumerate}
The basic flow resumes at step 8. \\

 &\textbf{A6: Manage Symptoms.} Step 5 of basic flow:
\begin{enumerate}
\item[6.] The user selects symptom tracking.
\item[7.] The system retrieves symptom data.
\item[8.] The system processes symptom information.
\item[9.] The system displays symptom overview and patterns.
\item[10.] The user may select to record new symptoms or view symptom history.
\end{enumerate}
The basic flow resumes at step 8. \\

\hline
\textbf{Exceptional flow} & 
\textbf{E1: No data available.} Step 2 of basic flow:
\begin{enumerate}
\item[3.] The system detects no health data is available.
\item[4.] The system displays empty state with guidance to start tracking health data.
\end{enumerate}
The basic flow resumes at step 5. \\
\hline
\textbf{Post-condition} & The user has viewed their health dashboard information. \\
\hline
\end{longtable}