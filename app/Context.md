# ADL App User Workflow

## 1. Onboarding & Setup
- **Sign Up / Log In**:
    - Users sign up or log in using their email or phone number.
    - Account creation process includes basic verification (email or phone).
- **Basic Details**:
    - Users enter personal information such as:
        - Full name
        - Age
        - Health conditions (optional)
        - Emergency contacts (name, phone number, relationship)
- **Customization**:
    - Users customize settings to enhance usability:
        - **Voice Control**: Enable or disable voice control for app interaction.
        - **Reminders**: Set reminders for medications, hydration, movement, etc.
        - **Wearable Integration**: Option to connect a wearable device for real-time health data synchronization.
        - **Accessibility Features**: Adjust text size, background color, etc.

## 2. Home Dashboard
- **Overview**: The home dashboard displays key user information:
    - **Step Count**: Real-time count of steps taken.
    - **Upcoming Reminders**: Upcoming medication, hydration, or movement reminders.
    - **Health AI Chat Shortcut**: A button to quickly access the AI chat for health-related queries.
    - **Mood Check-In**: Users can input their daily mood using:
        - Emoji-based response (e.g., happy, sad, neutral).
        - Optional short text description (e.g., “Feeling tired today”).
        - This is used to track emotional well-being over time.
- **SOS Button**:
    - Always accessible from the dashboard for emergencies.
    - One tap sends an emergency message with the user’s location to contacts.

## 3. Health AI Chat
- **Interaction**: Users can ask questions about health and well-being through text or voice.
    - Voice input can be activated by pressing a microphone icon.
    - Text input is also supported for users who prefer typing.
- **Personalized Advice**: The AI provides tailored responses based on:
    - User health data (e.g., "Time to take your medication").
    - Activity level or lack of activity ("You haven’t moved much today, how about a short walk?").
- **Image Input**: Users can upload images to the chat, which the AI can analyze and respond to (e.g., for food logging or symptoms).
    - The app prompts users for image input when relevant (e.g., "Send an image of your meal").

## 4. Smart Reminders & Notifications
- **Adaptive Reminders**:
    - Medication, hydration, and movement reminders adjust based on user behavior:
        - **If reminders are ignored** multiple times, the app sends notifications to caregivers (if enabled).
        - Users can mark reminders as complete to reset the frequency of reminders.
        - **Reminder Snooze Option**: Users can snooze reminders for a set period.
- **Notifications**:
    - Push notifications alert users about upcoming tasks and reminders.
    - Option to receive reminders via both app and SMS/email (depending on preferences).

## 5. AI-Based Activity Recommendations
- **Activity Suggestions**:
    - The app suggests physical or mental health activities based on the user's inactivity or mood:
        - If no activity is tracked for a set period, the app suggests a short walk or stretches.
        - If a negative mood trend is detected, the app might suggest relaxation or mindfulness activities.
- **Customizable Preferences**: Users can set preferences for what kinds of activities they prefer (e.g., physical vs. mental wellness, preferred types of exercises).

## 6. Step Counter & Health Tracking
- **Step Tracking**:
    - Tracks daily steps and activity levels.
    - Displays graphs and trends over time.
- **Wearable Integration**:
    - If connected to a wearable device (e.g., Fitbit, Apple Watch), the app syncs real-time health data such as heart rate, sleep patterns, and activity.
- **Health Insights**:
    - Displays insights based on user activity, such as “You’ve taken 10,000 steps today!” or “You haven’t reached your hydration goal today.”

## 7. Emergency Handling
- **SOS Button**:
    - One tap to send an emergency message, which includes:
        - A pre-written emergency message (e.g., “I need help”).
        - User’s current GPS location.
        - The message is sent to the user’s emergency contacts via SMS and/or email.
- **Send Location**:
    - Location data is sent along with the SOS message to ensure quick response from emergency contacts.
- **Emergency Contacts**:
    - Users can define multiple emergency contacts during onboarding or in the app settings.
    - Contacts are notified via SMS with a map link to the user’s location.

---

### Additional Features:

- **Voice Control**:
    - The app includes voice control for hands-free operation.
    - Voice control can be used to:
        - Set reminders (e.g., “Set a reminder to take my medicine at 6 PM”).
        - Request health updates (e.g., “How many steps have I walked today?”).
        - Trigger AI chat (e.g., “Talk to the health assistant”).
    - Voice recognition is powered by Android’s SpeechRecognizer API or third-party services like Google Cloud Speech-to-Text.

- **Mood Check-in on Dashboard**:
    - Daily mood tracking is integrated directly into the dashboard, making it quick and easy for users to track their emotions and mental health.
    - If a trend of negative moods is detected, the app may suggest relaxation techniques or prompt the user to take action to improve their well-being.

---

This detailed workflow covers the app’s features and user journey, emphasizing accessibility, ease of use, and personalized interaction for users with ADLs. Let me know if you need more details or modifications!
