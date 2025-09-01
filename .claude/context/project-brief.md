---
created: 2025-08-31T13:03:35Z
last_updated: 2025-08-31T13:03:35Z
version: 1.0
author: Claude Code PM System
---

# Project Brief

## Executive Summary

**BigTime** is an open-source Android application that recreates the functionality and design of the popular Days Matter (倒数日) app. It helps users track important dates by showing countdown timers for future events and day counters for past events, presented in a visually distinctive card-based interface.

## Project Purpose

### What It Does
- Tracks important dates and events in users' lives
- Calculates and displays days until future events (countdown)
- Calculates and displays days since past events (count-up)
- Organizes events by categories for easy management
- Provides at-a-glance information through widgets and notifications

### Why It Exists
- Provide a free, open-source alternative to Days Matter
- Offer privacy-focused date tracking without ads or data collection
- Create a customizable platform for personal date management
- Support both English and Chinese-speaking users (planned)

## Key Objectives

### Primary Goals
1. **Feature Parity**: Achieve core functionality matching Days Matter app
2. **User Experience**: Maintain intuitive, visually appealing interface
3. **Performance**: Ensure smooth, responsive app performance
4. **Reliability**: Provide stable, crash-free experience

### Secondary Goals
1. **Localization**: Support multiple languages and calendar systems
2. **Extensibility**: Build modular architecture for future features
3. **Community**: Foster open-source contribution and feedback
4. **Privacy**: Maintain user data privacy and security

## Success Criteria

### Functional Requirements
- ✅ Users can create, edit, and delete events
- ✅ Events display accurate day counts
- ✅ Categories organize events effectively
- ⚠️ Reminders notify users of upcoming events
- ⚠️ Data persists across app restarts
- ❌ Events can repeat on schedule

### Quality Requirements
- App launches in under 2 seconds
- Zero data loss during normal operation
- Crash rate below 1%
- Memory usage under 100MB
- Battery impact negligible

### User Satisfaction
- Intuitive interface requiring no tutorial
- Visual parity with Days Matter aesthetic
- Responsive to user interactions
- Customizable to user preferences

## Project Scope

### In Scope
- Event creation and management
- Day counting calculations
- Category organization
- Local data storage
- Widget support
- Reminder notifications
- Basic customization options
- Dark theme support

### Out of Scope (Current Phase)
- Cloud synchronization
- Social sharing features
- Advanced analytics
- Third-party integrations
- Paid premium features
- Cross-platform support

## Target Audience

### Primary Users
- Individuals tracking personal milestones
- Students managing academic deadlines
- Professionals tracking project timelines
- Couples celebrating anniversaries
- Parents monitoring child development

### User Characteristics
- Age range: 18-45 years
- Technical proficiency: Basic to intermediate
- Primary use: Personal organization
- Device: Android smartphones (7.0+)

## Technical Requirements

### Platform Requirements
- Android 7.0 (API 24) minimum
- Android 15 (API 36) target
- Kotlin programming language
- Material Design guidelines

### Architecture Requirements
- MVVM architecture pattern
- Room database for persistence
- Coroutines for async operations
- Single activity architecture

## Timeline & Milestones

### Completed (v1.0.1)
- ✅ Basic app structure
- ✅ Database setup
- ✅ Core UI implementation
- ✅ Event creation functionality

### Current Sprint
- 🔄 Complete category filtering
- 🔄 Implement event editing
- 🔄 Add deletion functionality
- 🔄 Fix existing bugs

### Next Release (v1.1.0)
- Reminder notifications
- Repeat events
- Data backup/restore
- Lunar calendar support

### Future Releases
- Cloud synchronization
- Advanced customization
- Social features
- Premium features

## Constraints & Assumptions

### Constraints
- Single developer resource
- No dedicated design team
- Limited testing devices
- No marketing budget
- Open-source commitment

### Assumptions
- Users have Android 7.0+ devices
- Internet not required for core features
- Users understand basic app navigation
- Local storage sufficient for user needs

## Risk Assessment

### Technical Risks
- **Database migration failures**: Medium risk, high impact
- **Memory leaks**: Low risk, medium impact
- **Widget performance**: Medium risk, low impact

### Market Risks
- **Competition from established apps**: High risk, medium impact
- **User adoption challenges**: Medium risk, high impact
- **Platform changes**: Low risk, medium impact

### Mitigation Strategies
- Implement comprehensive testing
- Follow Android best practices
- Maintain active user feedback loop
- Regular updates and bug fixes

## Resources

### Development Resources
- GitHub repository for version control
- Android Studio IDE
- Physical and virtual test devices
- Community feedback channels

### Knowledge Resources
- Days Matter app for reference
- Android documentation
- Material Design guidelines
- Open-source community

## Deliverables

### Current Deliverables
- Functional Android application
- Source code repository
- Basic documentation
- APK distribution

### Future Deliverables
- Comprehensive user documentation
- Contribution guidelines
- API documentation
- Localization files

## Success Metrics

### Quantitative Metrics
- 1000+ active users within 6 months
- 4.0+ star rating on app stores
- <1% crash rate
- 80% feature completion

### Qualitative Metrics
- Positive user feedback
- Active community engagement
- Code quality improvements
- Feature request satisfaction