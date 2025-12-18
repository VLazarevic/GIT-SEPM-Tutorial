import {HttpClient, HttpParams} from '@angular/common/http';
import {Injectable} from '@angular/core';
import {map, Observable} from 'rxjs';
import {environment} from 'src/environments/environment';
import {Horse, HorseCreate, HorseFamily, HorseSearch, HorseUpdate} from '../dto/horse';
import {formatIsoDate} from "../utils/date-helper";


const baseUri = environment.backendUrl + '/horses';

@Injectable({
  providedIn: 'root'
})
export class HorseService {

  constructor(
    private http: HttpClient
  ) {
  }

  /**
   * Get all horses stored in the system
   *
   * @return observable list of found horses.
   */
  getAll(): Observable<Horse[]> {
    return this.http.get<Horse[]>(baseUri)
      .pipe(
        map(horses => horses.map(this.fixHorseDate))
      );
  }

  getById(id: number): Observable<Horse> {
    return this.http.get<Horse>(`${baseUri}/${id}`)
      .pipe(
        map(this.fixHorseDate)
      );
  }

  /**
   * Create a new horse in the system.
   *
   * @param horse the data for the horse that should be created
   * @return an Observable for the created horse
   */
  create(horse: HorseCreate): Observable<Horse> {
    console.log(horse);
    // Cast the object to any, so that we can circumvent the type checker.
    // We _need_ the date to be a string here, and just passing the object with the
    // “type error” to the HTTP client is unproblematic

    const formData = new FormData();

    formData.append('name', horse.name);
    formData.append('description', horse.description || '');
    formData.append('dateOfBirth', formatIsoDate(horse.dateOfBirth));
    formData.append('sex', horse.sex);


    if (horse.ownerId) {
      formData.append('ownerId', horse.ownerId.toString());
    }

    if (horse.image) {
      formData.append('image', horse.image);
    }

    if (horse.motherId) {
      formData.append('motherId', horse.motherId.toString());
    }
    if (horse.fatherId) {
      formData.append('fatherId', horse.fatherId.toString());
    }

    return this.http.post<Horse>(
      baseUri,
      formData
    ).pipe(
      map(this.fixHorseDate)
    );
  }

  /**
   * Create a new horse in the system.
   *
   * @param horse the data for the horse that should be created
   * @return an Observable for the created horse
   */
  update(horse: HorseUpdate): Observable<Horse> {
    console.log(horse);
    // Cast the object to any, so that we can circumvent the type checker.
    // We _need_ the date to be a string here, and just passing the object with the
    // “type error” to the HTTP client is unproblematic

    const formData = new FormData();

    formData.append('name', horse.name);
    formData.append('description', horse.description || '');
    formData.append('dateOfBirth', formatIsoDate(horse.dateOfBirth));
    formData.append('sex', horse.sex);


    if (horse.ownerId) {
      formData.append('ownerId', horse.ownerId.toString());
    }

    if (horse.image) {
      formData.append('image', horse.image);
    }

    if (horse.motherId) {
      formData.append('motherId', horse.motherId.toString());
    }
    if (horse.fatherId) {
      formData.append('fatherId', horse.fatherId.toString());
    }

    return this.http.put<Horse>(
      baseUri + '/' + horse.id,
      formData
    ).pipe(
      map(this.fixHorseDate)
    );
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${baseUri}/${id}`);
  }

  getFamilyById(horseId: number, gen: number): Observable<HorseFamily> {
    return this.http.get<HorseFamily>(baseUri + '/' + horseId + '/family', {params: {gen}})
  }

  searchHorses(searchParam: HorseSearch): Observable<Horse[]> {
    let httpPar = new HttpParams();
    const temp: any = searchParam;
    for (const key in temp) {
      if (temp[key] !== undefined) {
        httpPar = httpPar.set(key, temp[key]);
      }
    }
    return this.http.get<Horse[]>(baseUri, { params: httpPar });
  }

  private fixHorseDate(horse: Horse): Horse {
    // Parse the string to a Date
    horse.dateOfBirth = new Date(horse.dateOfBirth as unknown as string);
    return horse;
  }

}
