# Question Upload Template

This CSV template allows admins to bulk upload questions to the Pilot Quiz Platform.

## CSV Format

```csv
question_text,explanation,category,difficulty,rating_type,answer_1,answer_1_correct,answer_2,answer_2_correct,answer_3,answer_3_correct,answer_4,answer_4_correct
"What is the minimum visibility required for VFR flight in Class E airspace below 10,000 feet MSL?","FAR 91.155 requires 3 statute miles visibility for VFR flight in Class E airspace below 10,000 feet MSL.",Weather Minimums,MEDIUM,PRIVATE,3 statute miles,true,1 statute mile,false,5 statute miles,false,2 statute miles,false
```

## Column Definitions

| Column | Required | Description | Valid Values |
|--------|----------|-------------|--------------|
| question_text | ✓ | The question content | Text |
| explanation | ✓ | Explanation shown after answer | Text |
| category | ✓ | Topic category | See categories below |
| difficulty | ✓ | Question difficulty | EASY, MEDIUM, HARD |
| rating_type | ✓ | Target pilot rating | PRIVATE, INSTRUMENT, COMMERCIAL, ATP |
| answer_1-4 | ✓ | Answer options (min 2, max 4) | Text |
| answer_X_correct | ✓ | Is this the correct answer? | true, false |

## Categories

- **Regulations** - FARs, AIM, NOTAMs
- **Weather** - Meteorology, weather services
- **Navigation** - Charts, instruments, procedures
- **Aircraft Systems** - Engines, electrical, hydraulics
- **Aerodynamics** - Principles of flight
- **Human Factors** - Aeromedical, decision making
- **Emergency Procedures** - Abnormal/emergency ops
- **Flight Planning** - Weight & balance, performance

## Sample Questions (10 Aviation Examples)

```csv
question_text,explanation,category,difficulty,rating_type,answer_1,answer_1_correct,answer_2,answer_2_correct,answer_3,answer_3_correct,answer_4,answer_4_correct
"What is the minimum flight visibility for VFR operations in Class E airspace below 10,000 feet MSL during the day?","FAR 91.155 specifies 3 SM visibility and 500 below, 1000 above, 2000 horizontal cloud clearance for Class E daytime below 10,000 feet.",Regulations,MEDIUM,PRIVATE,3 statute miles,true,1 statute mile,false,5 statute miles,false,1/2 statute mile,false
"During a preflight inspection, you notice frost on the wings. What action should you take?","Frost disrupts the smooth airflow over the wing and can reduce lift by 30% or more. All frost must be removed before flight.",Aircraft Systems,EASY,PRIVATE,Remove all frost before flight,true,Frost is acceptable if less than 1/4 inch,false,Taxi to deicing area and fly immediately,false,Apply heat from engine exhaust,false
"What causes an aircraft to enter a spin?","A spin is an aggravated stall that results in autorotation. It occurs when one wing is more stalled than the other, causing the aircraft to rotate around its vertical axis while descending.",Aerodynamics,MEDIUM,PRIVATE,Aggravated stall with autorotation,true,Excessive airspeed in a turn,false,Engine failure during climb,false,Improper weight distribution,false
"When must a pilot file an IFR flight plan?","14 CFR 91.173 requires an IFR flight plan for flight in controlled airspace under IFR conditions.",Regulations,EASY,INSTRUMENT,Before operating under IFR in controlled airspace,true,Only for flights above 18,000 feet MSL,false,Only when visibility is below 1 mile,false,Only for commercial operations,false
"What does a steady red light from the control tower signify to an aircraft on the ground?","A steady red light signal to aircraft on the ground means STOP. The pilot must hold position and wait for further instructions.",Regulations,EASY,PRIVATE,Stop,true,Cleared for takeoff,false,Taxi clear of runway,false,Return to starting point,false
"What is the purpose of a VOR check?","VOR receiver accuracy must be checked within 30 days before flight under IFR per 14 CFR 91.171. Ground checks allow ±4° and airborne checks allow ±6°.",Navigation,MEDIUM,INSTRUMENT,To verify VOR receiver accuracy before IFR flight,true,To check fuel quantity,false,To test engine magnetos,false,To verify altimeter settings,false
"During cruise flight, you experience engine roughness. What is your first action?","Carburetor icing can cause engine roughness. Apply carburetor heat first to rule out ice as the cause before investigating other issues.",Emergency Procedures,MEDIUM,PRIVATE,Apply carburetor heat,true,Increase mixture to full rich,false,Immediately declare emergency,false,Reduce power to idle,false
"What is the significance of a red and white sectional chart symbol?","A red and white symbol indicates an airport with control tower. Magenta indicates airports without towers.",Navigation,EASY,PRIVATE,Airport with control tower,true,Private airport,false,Heliport,false,Seaplane base,false
"Hypoxia symptoms at 10,000 feet MSL typically include?","Hypoxia at moderate altitudes causes impaired judgment, euphoria, and reduced color vision. Symptoms are insidious and the pilot may not recognize impairment.",Human Factors,MEDIUM,COMMERCIAL,Impaired judgment and euphoria,true,Severe chest pain,false,Immediate unconsciousness,false,Tunnel vision only,false
"What is the maximum holding speed below 6,000 feet MSL?","The FAA mandates maximum holding speeds of 200 KIAS below 6,000 feet MSL to ensure aircraft remain within protected airspace.",Navigation,MEDIUM,INSTRUMENT,200 KIAS,true,230 KIAS,false,265 KIAS,false,No speed limit,false
```
