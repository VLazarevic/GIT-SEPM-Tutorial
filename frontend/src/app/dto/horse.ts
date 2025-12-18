import {Owner} from './owner';
import {Sex} from './sex';

export interface Horse {
  id?: number;
  name: string;
  description?: string;
  dateOfBirth: Date;
  sex: Sex;
  owner?: Owner;
  imageId?: number;
  image?: File;
  motherId?: number;
  fatherId?: number;
  mother?: Horse;
  father?: Horse;
}

export interface HorseSearch {
  name?: string;
  description?: string;
  dateOfBirth?: Date;
  sex?: Sex;
  owner?: Owner;
  limit?: number;
}

export interface HorseCreate {
  name: string;
  description?: string;
  dateOfBirth: Date;
  sex: Sex;
  ownerId?: number;
  image?: File;
  motherId?: number;
  fatherId?: number;
}

export interface HorseFamily {
  id: number;
  name: string;
  dateOfBirth: string;
  mother: HorseFamily | null;
  father: HorseFamily | null;
}

export function convertFromHorseToCreate(horse: Horse): HorseCreate {
  return {
    name: horse.name,
    description: horse.description,
    dateOfBirth: horse.dateOfBirth,
    sex: horse.sex,
    ownerId: horse.owner?.id,
    image: horse.image,
    motherId: horse.mother?.id,
    fatherId: horse.father?.id,
  };
}

export interface HorseUpdate {
  id: number;
  name: string;
  description?: string;
  dateOfBirth: Date;
  sex: Sex;
  ownerId?: number;
  motherId?: number;
  fatherId?: number;
  image?: File;
}

export function convertFromHorseToUpdate(horse: Horse): HorseUpdate {
  return {
    id: horse.id!,
    name: horse.name,
    description: horse.description,
    dateOfBirth: horse.dateOfBirth,
    sex: horse.sex,
    ownerId: horse.owner?.id,
    image: horse.image,
    motherId: horse.mother?.id,
    fatherId: horse.father?.id,
  };
}

