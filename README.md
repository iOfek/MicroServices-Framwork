# MicroServices-Framwork
A simple Micro-Service framework, simulating Artificial Intellegence model training using multiple CPUs and GPUs.

Students can create the “TrainModel” event in order to start training a model.
When a student finishes training a model, he can create another event
“TestModel”, which will be handled by the GPU, and will return results.
if the results are good he can publish its results.
Results are submitted to a new type of Micro-Service, `Conference’, each conference will
aggregate results from students, and on a set predefined time, will Broadcast the
aggregated results to all the students.

Events are prcocessed in Round-robin manner

Input File:
The input will be in JSON format, you can read about its syntax here, you are free to handle
it with any library you wish, however we recommend using the GSON library.
The input file json will contain the following fields:
● students - Array of students (with the fields described above), each will have an array
containing the models the student intends to train and publish.
● GPUS - Array of GPU types in the systems.
● CPUS - Array of CPUS in the system, each with its cores.
● Conferences - Array conferences, each with its name and time of publication.
● TickTime - will include the time each tick will take (milliseconds).
● Duration - The duration of running the process.
Output File:
Your output will be a text file, containing:
● Each student name, with the details of the the models he trained, and which one of
them has been published, in addition to the number of papers he has read.
● Each conference and its publications (e.g. name of models it published).
● GPU time used.
● CPU time used.
● Amount of batches processed by the CPUs.
Program Execution:
The program will start by parsing the input and constructing the system.
Each microservice will subscribe to its appropriate events/broadcasts, and the TimeService
will start the clock. The students will send their models for training, and will only send the
next model when the previous one finishes the entire process - Train, Test and Publish (if
needed).
Conferences will aggregate publications from students, and at a set time (according to the
input file) will publish all the results.
The program will finish execution according to the duration set by the input file, and will
create the output file.

How To Run:
mvn clean install
java -cp target/spl221ass2-1.0-jar-with-dependencies.jar bgu.spl.mics.application.CRMSRunner example_input.json
