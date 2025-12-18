import {HttpClient} from "@angular/common/http";
import {Injectable} from "@angular/core";
import {environment} from "../../environments/environment";

const baseUri = environment.backendUrl + '/images';

@Injectable({
  providedIn: 'root'
})
export class ImageService {

  constructor(
    private http: HttpClient,
  ) {
  }

  public getImageUrl(id: number) {
    return baseUri + '/' + id;
  }
}
